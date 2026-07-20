# Design Decisions

This explains *why* the code is structured the way it is - the reasoning behind each design
choice, not just where it's used. See [class-diagram.png](class-diagram.png) for how the pieces
connect to each other.

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
`ProjectService`, `Task[]` in `Project`, `User[]` in `UserService`) is a plain fixed-capacity
array with a separate `count` field tracking how much of it is actually in use. Lookups by ID
(`ProjectService.findProject`, `Project.findTask`, `TaskService.getTaskInProject`) are linear
scans - O(n) - which is the correct, honest complexity for an unsorted array with no index.
Given the small expected size per project/system in this lab, that's an acceptable trade-off,
not an oversight; a future lab replacing arrays with collections (per the spec's own roadmap)
would be the natural point to introduce hash-based lookups instead.

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

## Exception handling: typed exceptions instead of null returns

Before Phase 2, `ProjectService.findProject()` and `TaskService`'s lookups returned `null` when
nothing matched, trusting every caller to remember to check for it. That's exactly the kind of
thing that's easy to forget once and get a `NullPointerException` far from the actual cause,
with no indication in the stack trace that the real problem was an invalid ID entered earlier.
Six custom checked exceptions replace that pattern - `ProjectNotFoundException`,
`TaskNotFoundException`, `EmptyProjectException`, `InvalidProjectDataException`,
`InvalidUserDataException`, `InvalidInputException` - each named after the one specific failure
it represents, so a `catch` block documents exactly what it's handling without needing to
inspect a message string.

They're **checked** (extend `Exception`, not `RuntimeException`) on purpose: a checked exception
forces the compiler to guarantee every call site handles the failure, which is exactly this
lab's explicit objective ("implement exception handling using try-catch"). The trade-off is that
`throws` clauses ripple through constructors and service methods - accepted deliberately, since
it makes every failure path visible and compiler-enforced rather than optional.

Two of the six - `InvalidProjectDataException` and `InvalidUserDataException` - are guarded at
the model layer (`Project`'s and `User`'s constructors) even though the console UI's own
`ValidationUtils` already prevents bad data from ever reaching them. This is "defense in depth,"
not redundancy for its own sake: the model enforces its own invariant regardless of who's
calling it, so a future caller that bypasses the console entirely (a JUnit test constructing a
`Project` directly, for instance) still can't create an invalid object.

## Service layer refactor: UserService and a rescoped TaskService

`UserService` was extracted in Phase 2 to hold user storage/lookup (mirroring
`ProjectService`'s existing array + count pattern), pulling that responsibility out of `Main`
so the console-UI class isn't also managing a data store directly - a Single Responsibility fix.

`TaskService` changed shape rather than just being touched up: it originally searched *every*
project for a task ID (`findTaskAnywhere`, `findProjectOwningTask`). But every real call site
already knows which project it's working within - the user is always inside that project's
details screen when managing its tasks - so `getTaskInProject(Project, String)` takes the
project explicitly instead. This is simpler (no cross-project scanning) and safer: a task can
only be found/updated/removed through the project it actually belongs to, not through an
unrelated project's screen that happens to reuse the same task ID space.

## Retry-in-place instead of abort-to-menu

Early exception handling caught a failure, printed the error, and returned all the way back to
the main menu - so a single typo (a wrong project ID, an out-of-range menu choice) meant
re-navigating the whole menu tree again. `Main` now has small helpers -
`promptForProject(prompt)`, `promptForTask(project, prompt)` - that loop: ask, try, and on
failure print the error and ask again, rather than unwind the whole call stack. Entering `0`
cancels back out at any point, so the user is never trapped. The one deliberate exception:
`InvalidProjectDataException` in `addProject()` is *not* wrapped in a retry loop, because
`ValidationUtils` already guarantees valid input before that point - there's nothing a retry
would actually be retrying.

## Testing: why ValidationUtils is testable at all

`ValidationTests` exercises `ValidationUtils`'s retry-loop readers (`readPositiveInt`,
`readValidText`, etc.) without a real keyboard, by handing each one `new Scanner("badInput\nGoodInput\n")`
- a `Scanner` built directly from a `String` instead of `System.in`. This only works because
`ValidationUtils` took the `Scanner` as a **parameter** from the very first version of this
class, rather than hardcoding `System.in` internally - a Phase 1 design choice that had nothing
to do with testing at the time, but pays for itself directly now.

`ProjectTests` and `TaskTests` use `@BeforeEach` to rebuild a fresh object before every single
test method (JUnit's default `PER_METHOD` lifecycle already creates a new test-class instance
per test, but the fixture object itself still needs explicit rebuilding). This guarantees test
independence: a test that adds tasks to a project can never leak that state into a different
test that expects zero tasks, regardless of what order the tests happen to run in.

## Known scope decisions

- **Per-user performance summaries** - the spec itself marks this "(future expansion)" under
  Epic 4, so it's intentionally not implemented here.
- **No file/database persistence** - all data is in-memory for this lab, per the spec's stated
  scope; a future lab in the series is explicitly where persistence gets introduced.
- **Fixed array capacities** - `ProjectService` and `Project` cap storage at 100 projects / 50
  tasks per project respectively. This is a real limit of using arrays instead of collections
  (the array size has to be decided upfront), not a bug; it's large enough that it will never be
  hit in normal use of this lab.
- **No Maven/Gradle for JUnit 5** - the lab's stated tech stack is "Java 21, IntelliJ IDEA
  Community Edition, JUnit 5," with no build tool mentioned. JUnit 5 is added as a plain
  library instead (IntelliJ's own "Add JUnit5 to classpath" quick-fix, or a single downloaded
  jar from the command line - see the README's testing section) rather than introducing a
  build tool the assignment never asked for.
