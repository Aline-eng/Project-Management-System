# Java Project Management System

A console-based Project/Task Management System built with core Java (no collections, no
database) as the foundation lab for a multi-week Project Management series. All data lives
in-memory in arrays for the duration of a single run.

This lab builds on Lab 1 (Java Basics) by adding custom exception handling, a refactored
service layer, and a JUnit 5 test suite - while keeping every original feature working.

Built with **Java 21** in **IntelliJ IDEA Community Edition**, tested with **JUnit 5**.

## Setup and running

### Option A: IntelliJ IDEA

1. Open the project folder in IntelliJ (`File > Open`, select this directory).
2. Make sure the Project SDK is set to JDK 21 (`File > Project Structure > Project`).
3. Right-click `src/Main.java` and choose **Run 'Main.main()'**.

### Option B: Command line

From the project root:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")   # Windows PowerShell: javac -d out (Get-ChildItem -Recurse src\*.java).FullName
java -cp out Main
```

The app starts with 5 sample projects and 2 sample users already loaded, so you can explore
every feature immediately without any setup steps.

### Running the JUnit tests

No Maven/Gradle - JUnit 5 is added as a plain library, same as the rest of this project's setup.

**IntelliJ IDEA:** open any file under `test/` (e.g. `test/ProjectTests.java`) and right-click
inside it → **Run**. The first time you do this, IntelliJ offers to **"Add 'JUnit5' to
classpath"** via a lightbulb/banner - click it once and it downloads the JUnit 5 jars into a
local `lib/` folder (gitignored - every teammate does this once, locally, in their own IDE).
Every test class after that just works.

**Command line:** download the single `junit-platform-console-standalone` jar once from
[Maven Central](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar)
(this is a self-contained alternative to the `lib/` folder above - either works independently):

```bash
javac -cp junit-platform-console-standalone-1.10.2.jar -d out $(find src test -name "*.java")
java -jar junit-platform-console-standalone-1.10.2.jar --classpath out --scan-classpath
```

### Default users

| ID | Name | Role | Can update/delete tasks? |
|---|---|---|---|
| USR001 | Alice Johnson | Admin | Yes |
| USR002 | Bob Kariuki | Regular | No (view/add only) |

The app starts logged in as Alice (Admin). Use **Main Menu > 4. Manage Users > Switch User** to
switch to Bob and see the `(Admin only)` restrictions in action on **Update Task Status** /
**Remove Task**. New users can be registered via **Manage Users > Add New User** - name and
email are validated (a blank name or malformed email re-prompts instead of crashing).

## Project structure

```
src/
├── Main.java                          # Entry point - menu-driven console UI
├── enums/
│   ├── ProjectType.java               # SOFTWARE, HARDWARE
│   └── TaskStatus.java                # PENDING, IN_PROGRESS, COMPLETED
├── interfaces/
│   └── Completable.java               # isCompleted() contract
├── models/
│   ├── StatusReport.java              # One row of a status report
│   ├── Task.java                      # Task model (implements Completable)
│   ├── project/
│   │   ├── Project.java               # Abstract base class
│   │   ├── SoftwareProject.java       # Concrete project type
│   │   └── HardwareProject.java       # Concrete project type
│   └── user/
│       ├── User.java                  # Abstract user base
│       ├── RegularUser.java           # Concrete user type
│       └── AdminUser.java             # Concrete user type
├── services/
│   ├── ProjectService.java            # Project storage + lookup/filtering
│   ├── TaskService.java               # Task lookup/removal, scoped to one project
│   ├── ReportService.java             # Completion reporting
│   └── UserService.java               # User storage + lookup
└── utils/
    ├── ConsoleMenu.java                # Header/divider/pause/error-print helpers
    ├── ValidationUtils.java            # Input validation retry loops
    └── exceptions/
        ├── EmptyProjectException.java
        ├── InvalidInputException.java
        ├── InvalidProjectDataException.java
        ├── InvalidUserDataException.java
        ├── ProjectNotFoundException.java
        └── TaskNotFoundException.java

test/
├── ProjectTests.java                   # Construction validation + completion %
├── TaskTests.java                      # Status updates + assignment
└── ValidationTests.java                # ValidationUtils retry-loop readers
```

See [docs/class-diagram.png](docs/class-diagram.png) for how these classes relate, and
[docs/design-decisions.md](docs/design-decisions.md) for the reasoning behind each choice.

## Features mapped to user stories

| User Story / Feature | Status | Where |
|---|---|---|
| US-1.1 Browse Projects | Done | `Main.viewProjectCatalog()`, `Project.displayProject()` |
| US-1.2 Search Projects by Budget Range | Done | `ProjectService.searchByBudgetRange()` |
| Feature 1: Validate project data, enforce positive budget | Done | `Project` constructor throws `InvalidProjectDataException` |
| US-2.1 Add Task to Project | Done | `Main.addTaskToProject()` (duplicate names + unique IDs) |
| US-2.2 Update Task Status | Done | `Main.updateTaskStatus()` (Admin only) |
| Feature 2: Handle invalid task assignments | Done | `TaskService.getTaskInProject()` throws `TaskNotFoundException` |
| US-3.1 Create User Profiles | Done | `AdminUser`/`RegularUser`, auto-generated `USR###` IDs |
| Feature 3: Assign users to projects/tasks | Done | `Task.assignedTo`, `Main.selectUser()` (optional - "0" leaves a task unassigned) |
| Feature 3: Refactor user logic into a service | Done | `UserService` (storage/lookup), separated from `Main`'s UI code |
| Feature 3: Validate email/role via exceptions | Done | `User` constructor throws `InvalidUserDataException`; live in **Manage Users > Add New User** |
| US-4.1 Calculate Project Completion Average | Done | `Project.getCompletionPercentage()`, `ReportService` |
| Feature 4: Validate projects with no tasks | Done | `ReportService.checkCompletion()` throws `EmptyProjectException` |
| US-5.1 Main Menu Navigation | Done | `Main.main()` menu loop with input validation |
| Feature 5: Exception handling + retry-in-place | Done | `Main.promptForProject()` / `promptForTask()` re-prompt instead of aborting to the menu |
| Epic 3: JUnit unit tests | Done | 23 tests across `test/ProjectTests.java`, `TaskTests.java`, `ValidationTests.java` |
| Epic 4: Git version control (branches + merges) | Done | See **Git workflow** below |
| Per-user performance summaries | Not implemented | Explicitly marked "future expansion" in the spec |

## Exception handling

Six custom checked exceptions live in `utils.exceptions`, each guarding one specific invariant:

| Exception | Thrown when | Caught in |
|---|---|---|
| `InvalidProjectDataException` | A project is constructed with a non-positive budget or team size | `Project`'s constructor (defense-in-depth - the console UI's own input validation already prevents this before construction) |
| `InvalidUserDataException` | A user is constructed with a blank name, malformed email, or blank role | `User`'s constructor; live-reachable via **Manage Users > Add New User** |
| `ProjectNotFoundException` | A project ID doesn't exist | `ProjectService.findProject()` |
| `TaskNotFoundException` | A task ID doesn't exist within the given project | `TaskService.getTaskInProject()` |
| `EmptyProjectException` | Checking completion % on a project with zero tasks | `ReportService.checkCompletion()` |
| `InvalidInputException` | A budget-range search has min > max, or a duplicate task name | `ProjectService.searchByBudgetRange()`, `Main.addTaskToProject()` |

Every one of these is caught at the menu layer and re-prompts the user in place (e.g. a bad
project ID re-asks for the ID right there) instead of aborting back to the main menu - the menu
loop never crashes on invalid input.

## Testing

**Automated (JUnit 5):** 23 tests across three files - construction validation and completion
percentage (zero tasks / partial / all complete) in `ProjectTests`, status transitions and
assignment in `TaskTests`, and retry-loop input validation in `ValidationTests` (each simulates
one invalid entry followed by a valid one via a `Scanner` built from a plain `String`).

**Manual:** all 9 scenarios from Lab 1's test plan were re-run end-to-end against the refactored
app - viewing/filtering projects, adding software and hardware projects, adding and viewing
tasks, updating task status, calculating completion percentages (including zero-task and
all-complete edge cases), invalid input handling (non-numeric and out-of-range menu choices,
bad project/task IDs), and sequential ID auto-generation (`PRJ00#`, `TSK00#`, `USR00#`). None of
them crash the app.

## Git workflow

Each phase of this lab lived on its own branch, merged into `main` via a pull request once
verified:

- `feature/lab2-phase1-refactor` - SOLID refactor of Lab 1's code (PR #4)
- `feature/lab2-phase2-exceptions` - custom exceptions, `UserService`, retry-in-place UX (PR #5)
- `feature/lab2-phase3-testing` - JUnit 5 test suite (PR #6)
- `feature/lab2-phase4-documentation` - this documentation update

Within each branch, commits are split by concern rather than one giant commit per phase - e.g.
Phase 2's exceptions were committed as one commit per exception group, not as a single squashed
change - so the history itself documents what changed and why.

Commit log snapshot (`git log --oneline --graph`):

```
*   019b6a9 Merge pull request #6 from Aline-eng/feature/lab2-phase3-testing
|\
| * 04e74b9 Added lib folder containing Jar files into gitignore
| * 2513166 docs(readme): add JUnit test setup instructions
| * cfc4adb test(validation): add JUnit tests for ValidationUtils retry-loop readers
| * 0fd8f0c test(task): add JUnit tests for status updates and assignment
| * d662f48 test(project): add JUnit tests for construction validation and completion %
|/
*   cd211b5 Merge pull request #5 from Aline-eng/feature/lab2-phase2-exceptions
|\
| * 7216e3a chore: gitignore local anki study decks
| * 17a7ec3 refactor(ui): retry in place on invalid input instead of aborting to the menu
| * 540cf67 feat(validation): reject pure-numeric/symbol-only text for name and description fields
| * 8c77bf2 feat(users): add UserService and a live "Manage Users" / "Add New User" flow
| * b66f4e3 feat(exceptions): validate user data on creation; unify duplicate-task check with the exception pattern
| * e88c888 feat(exceptions): validate project/task inputs, replace null-returns with typed exceptions
|/
*   a94c53c Merge pull request #4 from Aline-eng/feature/lab2-phase1-refactor
```

## OOP concepts demonstrated

- **Encapsulation** - every model's fields are private with controlled getters/setters.
- **Abstraction** - `Project` and `User` are abstract classes; `Completable` is an interface.
- **Inheritance** - `SoftwareProject`/`HardwareProject` extend `Project`;
  `RegularUser`/`AdminUser` extend `User`.
- **Polymorphism** - overriding (`getProjectDetails()`, `getPermissions()`, `canModify()`) and
  overloading (`Task`'s two constructors).
- **Interfaces** - `Task implements Completable`.
- **Enums over strings** - `TaskStatus` and `ProjectType` are closed, compiler-checked sets of
  constants instead of free-text fields.
- **Arrays and linear search** - all storage is fixed-size arrays with a tracked count, per the
  lab's scope; lookups by ID are linear scans, which is the correct complexity for this data
  structure.

Full rationale for each of these: [docs/design-decisions.md](docs/design-decisions.md).
