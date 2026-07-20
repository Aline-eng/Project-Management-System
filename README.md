# Java Project Management System

A console-based Project/Task Management System built with core Java (no collections, no
database) as the foundation lab for a multi-week Project Management series. All data lives
in-memory in arrays for the duration of a single run.

Built with **Java 21** in **IntelliJ IDEA Community Edition**.

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
inside it → **Run**. The first time you do this, IntelliJ will show a banner/lightbulb offering
to **"Add 'JUnit5' to classpath"** - click it once (it downloads the library automatically) and
every test class after that just works.

**Command line:** download the single `junit-platform-console-standalone` jar once from
[Maven Central](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar),
then:

```bash
javac -cp junit-platform-console-standalone-1.10.2.jar -d out $(find src test -name "*.java")
java -jar junit-platform-console-standalone-1.10.2.jar --classpath out --scan-classpath
```

### Default users

| ID | Name | Role | Can update/delete tasks? |
|---|---|---|---|
| USR001 | Alice Johnson | Admin | Yes |
| USR002 | Bob Kariuki | Regular | No (view/add only) |

The app starts logged in as Alice (Admin). Use **Main Menu > 4. Switch User** to switch to Bob
and see the `(Admin only)` restrictions in action on **Update Task Status** / **Remove Task**.

## Project structure

```
src/
├── Main.java                     # Entry point - menu-driven console UI
├── models/
│   ├── Project.java               # Abstract base class
│   ├── SoftwareProject.java       # Concrete project type
│   ├── HardwareProject.java       # Concrete project type
│   ├── ProjectType.java           # Enum: SOFTWARE, HARDWARE
│   ├── Task.java                  # Task model (implements Completable)
│   ├── TaskStatus.java             # Enum: PENDING, IN_PROGRESS, COMPLETED
│   ├── User.java                  # Abstract user base
│   ├── RegularUser.java           # Concrete user type
│   ├── AdminUser.java             # Concrete user type
│   └── StatusReport.java          # One row of a status report
├── interfaces/
│   └── Completable.java           # isCompleted() contract
├── services/
│   ├── ProjectService.java        # Project storage + lookup/filtering
│   ├── TaskService.java           # Cross-project task lookup/removal
│   └── ReportService.java         # Completion reporting
└── utils/
    ├── ConsoleMenu.java           # Header/divider/pause helpers
    └── ValidationUtils.java       # Input validation loops
```

See [docs/class-diagram.png](docs/class-diagram.png) for how these classes relate, and
[docs/design-decisions.md](docs/design-decisions.md) for the reasoning behind each OOP choice.

## Features mapped to user stories

| User Story | Status | Where |
|---|---|---|
| US-1.1 Browse Projects | Done | `Main.viewProjectCatalog()`, `Project.displayProject()` |
| US-1.2 Search Projects by Budget Range | Done | `ProjectService.searchByBudgetRange()` |
| US-2.1 Add Task to Project | Done | `Main.addTaskToProject()` (duplicate names + unique IDs) |
| US-2.2 Update Task Status | Done | `Main.updateTaskStatus()` (Admin only) |
| US-3.1 Create User Profiles | Done | `AdminUser`/`RegularUser`, auto-generated `USR###` IDs |
| Feature 3: Assign users to projects/tasks | Done | `Task.assignedTo`, `Main.selectUser()` |
| US-4.1 Calculate Project Completion Average | Done | `Project.getCompletionPercentage()`, `ReportService` |
| US-5.1 Main Menu Navigation | Done | `Main.main()` menu loop with input validation |
| Per-user performance summaries | Not implemented | Explicitly marked "future expansion" in the spec |

## Testing

All 9 scenarios from the lab's test plan were run end-to-end against the built console app:
viewing/filtering projects, adding software and hardware projects, adding and viewing tasks,
updating task status, calculating completion percentages (including zero-task and all-complete
edge cases), invalid input handling (non-numeric and out-of-range menu choices), and sequential
ID auto-generation (`PRJ00#`, `TSK00#`, `USR00#`). None of them crash the app; invalid input
always re-prompts instead.

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
