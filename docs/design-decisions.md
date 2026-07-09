# Design Decisions

This explains *why* the code is structured the way it is - the reasoning behind each OOP
concept used, not just where it's used. See [class-diagram.md](class-diagram.md) for how the
pieces connect to each other.

## Encapsulation

Every model (`Project`, `Task`, `User`, `StatusReport`) keeps its fields `private` and exposes
them only through getters/setters. Two places this actually matters, not just as a rule to
follow:

- `Project.getTasks()` returns a **copy** of its internal task array (`System.arraycopy`), not
  the array itself. If it returned the real reference, any caller could reach in and overwrite a
  slot directly, bypassing `addTask`/`removeTask` and corrupting `taskCount`.
- `TaskStatus` and `ProjectType` are enums instead of raw `String` fields. A `String status`
  field would compile happily with `task.setStatus("Donee")` (typo) or `"pending "` (stray
  space) - the bug only shows up at runtime, if it shows up at all. An enum is a closed,
  compiler-checked set of values, so invalid states are impossible by construction, not just
  caught by validation.

## Abstraction: abstract classes vs. the interface

`Project` and `User` are `abstract` classes; `Completable` is an `interface`. These aren't
interchangeable choices - each fits a different relationship:

- `Project` and `User` are abstract classes because `SoftwareProject`/`HardwareProject` and
  `RegularUser`/`AdminUser` **are** projects and users - they share real state (`id`, `name`,
  `budget`/`role`, etc.) and inherited behavior (`displayProject()`, `displayUserInfo()`), not
  just a method signature.
- `Completable` is an interface because "being completable" isn't an *is-a* relationship - it's
  a capability. `Task` doesn't need to inherit any shared state to implement it, and nothing
  stops a future unrelated class (e.g. a `Milestone`) from implementing `Completable` too
  without joining the `Project`/`Task` hierarchy at all. That's the actual reason to reach for an
  interface instead of another abstract class.

## Inheritance and polymorphism

`SoftwareProject` and `HardwareProject` both override `getProjectDetails()`; `RegularUser` and
`AdminUser` both override `getPermissions()` and `canModify()`. In every case, a method written
**once** in the parent (`displayProject()`, `displayUserInfo()`) calls the overridden method
through `this`, and Java resolves it to whichever subclass the actual object is, at runtime -
that's dynamic polymorphism. Concretely: `Project.displayProject()` never has an `if
(instanceof SoftwareProject)` check anywhere - it just calls `getProjectDetails()`, and the
correct type-specific string comes back automatically no matter which subclass it's called on.

`getProjectDetails()` deliberately returns only the project's type label (`"Software"` /
`"Hardware"`) rather than inventing extra per-type fields (a `primaryLanguage` or
`hardwareType` attribute, for example). The console UI examples in the spec never show such a
field, so adding one would mean displaying data with no basis in the given requirements. The
overriding/polymorphism is still fully real - two subclasses share one method name with two
different bodies - it's just scoped to data the project already has.

## Method overloading

`Task` has two constructors: `Task(name, status)` for an unassigned task, and `Task(name,
status, assignedTo)` for one created already assigned to a user. Same method name, different
parameter lists, resolved at compile time based on how many arguments are passed - this is
overloading (a compile-time decision) as distinct from overriding (a runtime decision covered
above). `Main.seedSampleData()` calls both forms side by side, which is a convenient place to
point at both if asked to demonstrate the difference.

## Composition and aggregation

A `Project` owns an array of `Task` objects directly (`Project` **has-a** `Task[]`, it does not
**extend** `Task`) - this is composition: the tasks live and die with their project. Contrast
this with `ProjectService`, which holds a `Project[]` but doesn't own the projects' lifecycle
the same way - projects are looked up and returned by reference, not managed exclusively through
the service. That distinction is why `Project.addTask()`/`removeTask()` exist in `Project`
itself rather than being pushed into a service class.

## Why arrays, and where linear search shows up

The lab specifies arrays, not `ArrayList`/collections, so every store here (`Project[]` in
`ProjectService`, `Task[]` in `Project`, the fixed 2-element `User[]` in `Main`) is a plain
fixed-capacity array with a separate `count` field tracking how much of it is actually in use.
Lookups by ID (`ProjectService.findProject`, `Project.findTask`,
`TaskService.findTaskAnywhere`) are linear scans - O(n) - which is the correct, honest
complexity for an unsorted array with no index. Given the small expected size per project/system
in this lab, that's an acceptable trade-off, not an oversight; a future lab replacing arrays
with collections (per the spec's own roadmap) would be the natural point to introduce
hash-based lookups instead.

## Enums with labels, not raw strings - and only where something uses them

Both `TaskStatus` and `ProjectType` store a human-readable `label` (`"In Progress"`,
`"Software"`) separate from the Java constant name (`IN_PROGRESS`, `SOFTWARE`), because Java
enum constants can't contain spaces or lowercase letters, but the console UI needs to display
exactly the text the spec's mockups show. `TaskStatus.fromLabel()` is the one place free-text
console input gets validated into a real enum value - if it doesn't match `Pending`, `In
Progress`, or `Completed` (case-insensitive), it returns `null` and the caller re-prompts.

One method that looked like it belonged here but didn't survive review: `TaskStatus` originally
had a `toString()` override returning the same thing as `getLabel()`. Every call site in the
codebase calls `.getLabel()` explicitly, so `toString()` never actually ran - it was removed.
The lesson generalizes: a "supporting" method that nothing calls isn't defensive design, it's
dead code, and it's worth grepping for actual call sites before assuming a method is pulling its
weight.

## Role-based access (Epic 3)

`User.canModify()` is abstract, overridden to `true` in `AdminUser` and `false` in
`RegularUser`. `Main` checks it before allowing **Update Task Status** and **Remove Task**, and
shows an `(Admin only)` hint next to both menu options when the current user can't use them.
Adding a project or a task has no such check, because the spec's Feature 3 description is
explicit: "Admin can delete/update; Regular can view/add."

## Known scope decisions

- **Per-user performance summaries** - the spec itself marks this "(future expansion)" under
  Epic 4, so it's intentionally not implemented here.
- **No file/database persistence** - all data is in-memory for this lab, per the spec's stated
  scope; a future lab in the series is explicitly where persistence gets introduced.
- **Fixed array capacities** - `ProjectService` and `Project` cap storage at 100 projects / 50
  tasks per project respectively. This is a real limit of using arrays instead of collections
  (the array size has to be decided upfront), not a bug; it's large enough that it will never be
  hit in normal use of this lab.
