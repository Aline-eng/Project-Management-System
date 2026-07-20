# Class Diagram Source

This is the up-to-date text description of every class in the system, current as of Phase 3
(exceptions + JUnit testing). Paste the Mermaid block below into a Claude conversation (or any
Mermaid-compatible tool) to regenerate `docs/class-diagram.png` so it reflects the current
codebase - the existing image predates `UserService`, the 6 custom exceptions, and the
`models/project` / `models/user` package split.

## What changed since the last diagram

- `Project`/`User` moved into their own packages: `models.project`, `models.user`.
- New class: `services.UserService` (storage/lookup for users, same pattern as `ProjectService`).
- `TaskService` no longer searches every project - it now takes the `Project` explicitly
  (`getTaskInProject(Project, String)`), scoped to one project.
- New package: `utils.exceptions`, with 6 custom checked exceptions.
- `enums.ProjectType` / `enums.TaskStatus` moved out of `models` into their own `enums` package.

## Mermaid diagram

```mermaid
classDiagram
    direction TB

    class Project {
        <<abstract>>
        -String id
        -String name
        -String description
        -double budget
        -int teamSize
        -Task[] tasks
        -int taskCount
        +Project(name, description, budget, teamSize) throws InvalidProjectDataException
        +getProjectDetails() ProjectType*
        +displayProject()
        +addTask(Task) boolean
        +findTask(String) Task
        +removeTask(String) boolean
        +hasTaskNamed(String) boolean
        +getCompletionPercentage() double
        +getCompletedTaskCount() int
        +getPendingTaskCount() int
    }
    class SoftwareProject {
        +getProjectDetails() ProjectType
    }
    class HardwareProject {
        +getProjectDetails() ProjectType
    }
    Project <|-- SoftwareProject
    Project <|-- HardwareProject
    Project "1" *-- "0..50" Task : owns

    class Task {
        -String id
        -String name
        -TaskStatus status
        -User assignedTo
        +Task(name, status)
        +Task(name, status, assignedTo)
        +isCompleted() boolean
        +displayTask()
    }
    class Completable {
        <<interface>>
        +isCompleted() boolean
    }
    Completable <|.. Task
    Task "0..1" --> "0..1" User : assignedTo

    class User {
        <<abstract>>
        -String id
        -String name
        -String email
        -String role
        +User(name, email, role) throws InvalidUserDataException
        +getPermissions() String*
        +canModify() boolean*
        +displayUserInfo()
    }
    class AdminUser {
        +getPermissions() String
        +canModify() boolean
    }
    class RegularUser {
        +getPermissions() String
        +canModify() boolean
    }
    User <|-- AdminUser
    User <|-- RegularUser

    class ProjectType {
        <<enumeration>>
        SOFTWARE
        HARDWARE
    }
    class TaskStatus {
        <<enumeration>>
        PENDING
        IN_PROGRESS
        COMPLETED
    }

    class ProjectService {
        -Project[] projects
        -int projectCount
        +addProject(Project) boolean
        +findProject(String) Project throws ProjectNotFoundException
        +getAllProjects() Project[]
        +getProjectsByType(ProjectType) Project[]
        +searchByBudgetRange(min, max) Project[] throws InvalidInputException
    }
    class TaskService {
        -ProjectService projectService
        +getTaskInProject(Project, String) Task throws TaskNotFoundException
        +removeTaskFromProject(Project, String) throws TaskNotFoundException
    }
    class ReportService {
        -ProjectService projectService
        +generateReports() StatusReport[]
        +calculateAverageCompletion() double
        +checkCompletion(Project) double throws EmptyProjectException
        +printStatusReport()
    }
    class UserService {
        -User[] users
        -int userCount
        +addUser(User) boolean
        +getAllUsers() User[]
    }
    class StatusReport {
        -String projectId
        -String projectName
        -int taskCount
        -int completedCount
        -double completionPercentage
        +displayRow()
    }

    ProjectService "1" o-- "0..100" Project
    TaskService ..> ProjectService
    ReportService ..> ProjectService
    ReportService ..> StatusReport : creates
    UserService "1" o-- "0..50" User

    class Main {
        -Scanner scanner
        -ProjectService projectService
        -TaskService taskService
        -ReportService reportService
        -UserService userService
        -User currentUser
        +main(String[])
    }
    Main ..> ProjectService : uses
    Main ..> TaskService : uses
    Main ..> ReportService : uses
    Main ..> UserService : uses

    class ValidationUtils {
        <<utility>>
        +readInt(Scanner, prompt) int
        +readPositiveInt(Scanner, prompt) int
        +readIntInRange(Scanner, prompt, min, max) int
        +readPositiveDouble(Scanner, prompt) double
        +readNonNegativeDouble(Scanner, prompt) double
        +readNonEmptyString(Scanner, prompt) String
        +readValidText(Scanner, prompt) String
        +readValidStatus(Scanner, prompt) TaskStatus
    }
    class ConsoleMenu {
        <<utility>>
        +printHeader(String)
        +printDivider()
        +pause(Scanner)
        +printError(Exception)
    }
    Main ..> ValidationUtils : uses
    Main ..> ConsoleMenu : uses

    class InvalidProjectDataException {
        <<exception>>
        thrown by Project's constructor
    }
    class InvalidUserDataException {
        <<exception>>
        thrown by User's constructor
    }
    class ProjectNotFoundException {
        <<exception>>
        thrown by ProjectService.findProject
    }
    class TaskNotFoundException {
        <<exception>>
        thrown by TaskService.getTaskInProject
    }
    class EmptyProjectException {
        <<exception>>
        thrown by ReportService.checkCompletion
    }
    class InvalidInputException {
        <<exception>>
        thrown by budget-range search and duplicate task name
    }
```

## Notes for whoever regenerates the image

- Group the 6 exception classes visually off to one side (e.g. a dashed box labeled
  `utils.exceptions`) rather than interleaving them with the model/service classes - they're
  cross-cutting, not part of the main inheritance hierarchy, and cluttering the diagram with
  full exception detail obscures the actual OOP relationships (inheritance, composition,
  interface implementation) that matter more for this diagram's purpose.
- The three inheritance pairs to make visually obvious: `Project → SoftwareProject/HardwareProject`,
  `User → AdminUser/RegularUser`, and the interface implementation `Task ..|> Completable`.
- `Project *-- Task` should read as composition (filled diamond) - tasks live and die with their
  project. `ProjectService o-- Project` / `UserService o-- User` should read as aggregation
  (hollow diamond) - the service holds references but doesn't own their lifecycle the same way.
