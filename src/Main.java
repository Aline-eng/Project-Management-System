import java.util.Scanner;

import models.user.AdminUser;
import models.project.HardwareProject;
import models.project.Project;
import models.user.RegularUser;
import models.project.SoftwareProject;
import models.Task;
import enums.TaskStatus;
import models.user.User;
import enums.ProjectType;
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import services.UserService;
import utils.ConsoleMenu;
import utils.ValidationUtils;
import utils.exceptions.EmptyProjectException;
import utils.exceptions.InvalidInputException;
import utils.exceptions.InvalidProjectDataException;
import utils.exceptions.InvalidUserDataException;
import utils.exceptions.ProjectNotFoundException;
import utils.exceptions.TaskNotFoundException;

/**
 * Application entry point: the menu-driven console UI wiring together every
 * model and service. Responsible for input/output only - business rules and
 * data live in the model and service classes.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProjectService projectService = new ProjectService();
    private static final TaskService taskService = new TaskService(projectService);
    private static final ReportService reportService = new ReportService(projectService);
    private static final UserService userService = new UserService();

    private static User currentUser;

    /**
     * Seeds sample data, then loops the main menu until the user exits.
     *
     * @throws InvalidProjectDataException never in practice - the seed data
     *         below is hardcoded and always valid. Declared because
     *         Project's and User's constructors are checked; if this ever
     *         DID fire, it would mean the seed data itself has a bug, so
     *         letting it propagate and halt the program loudly is correct.
     * @throws InvalidUserDataException never in practice, same reason as above.
     */
    public static void main(String[] args) throws InvalidProjectDataException, InvalidUserDataException {
        seedSampleData();
        boolean running = true;

        while (running) {
            ConsoleMenu.printHeader("JAVA PROJECT MANAGEMENT SYSTEM");
            System.out.println("\nCurrent User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            System.out.println("\nMain Menu:");
            System.out.println("-----------");
            System.out.println("1. Manage Projects");
            System.out.println("2. Manage Tasks");
            System.out.println("3. View Status Reports");
            System.out.println("4. Manage Users");
            System.out.println("5. Exit");

            int choice = ValidationUtils.readInt(scanner, "\nEnter your choice: ");

            switch (choice) {
                case 1:
                    manageProjects();
                    break;
                case 2:
                    manageTasks();
                    break;
                case 3:
                    manageReports();
                    break;
                case 4:
                    manageUsers();
                    break;
                case 5:
                    System.out.println("\nThank you for using the Java Project Management System!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-5.");
            }
        }
        scanner.close();
    }

    // ================= Epic 1: Project Catalog Management =================

    /** "Manage Projects" submenu: add a new project, or view/filter the catalog. */
    private static void manageProjects() {
        ConsoleMenu.printHeader("PROJECT CATALOG");
        System.out.println("\nOptions:");
        System.out.println("1. Add New Project");
        System.out.println("2. View / Filter Project Catalog");
        System.out.println("3. Back to Main Menu");

        int choice = ValidationUtils.readInt(scanner, "\nEnter your choice: ");
        switch (choice) {
            case 1:
                addProject();
                break;
            case 2:
                viewProjectCatalog();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /** Prompts for project details and creates a Software or Hardware project. */
    private static void addProject() {
        System.out.println("\nProject type:");
        System.out.println("1. Software Project");
        System.out.println("2. Hardware Project");
        int typeChoice = ValidationUtils.readInt(scanner, "Select type (1-2): ");

        String name = ValidationUtils.readNonEmptyString(scanner, "Enter project name: ");
        String description = ValidationUtils.readNonEmptyString(scanner, "Enter project description: ");
        double budget = ValidationUtils.readPositiveDouble(scanner, "Enter project budget: $");
        int teamSize = ValidationUtils.readPositiveInt(scanner, "Enter team size: ");

        Project project;
        try {
            if (typeChoice == 1) {
                project = new SoftwareProject(name, description, budget, teamSize);
            } else if (typeChoice == 2) {
                project = new HardwareProject(name, description, budget, teamSize);
            } else {
                System.out.println("❌ Invalid project type selected.");
                return;
            }
        } catch (InvalidProjectDataException e) {
            ConsoleMenu.printError(e);
            ConsoleMenu.pause(scanner);
            return;
        }

        projectService.addProject(project);
        System.out.println("\n✓ Project successfully created. (ID: " + project.getId() + ")");
        ConsoleMenu.pause(scanner);
    }

    /** Lists projects (optionally filtered), then offers to drill into one by ID. */
    private static void viewProjectCatalog() {
        ConsoleMenu.printHeader("PROJECT CATALOG");
        System.out.println("\nFilter Options:");
        System.out.println("1. View All Projects (" + projectService.getProjectCount() + ")");
        System.out.println("2. Software Projects Only");
        System.out.println("3. Hardware Projects Only");
        System.out.println("4. Search by Budget Range");

        int filterChoice = ValidationUtils.readInt(scanner, "\nEnter filter choice: ");
        Project[] results;

        try {
            switch (filterChoice) {
                case 2:
                    results = projectService.getProjectsByType(ProjectType.SOFTWARE);
                    break;
                case 3:
                    results = projectService.getProjectsByType(ProjectType.HARDWARE);
                    break;
                case 4:
                    double min = ValidationUtils.readNonNegativeDouble(scanner, "Enter minimum budget: $");
                    double max = ValidationUtils.readPositiveDouble(scanner, "Enter maximum budget: $");
                    results = projectService.searchByBudgetRange(min, max);
                    break;
                case 1:
                default:
                    results = projectService.getAllProjects();
            }
        } catch (InvalidInputException e) {
            ConsoleMenu.printError(e);
            ConsoleMenu.pause(scanner);
            return;
        }

        System.out.println();
        ConsoleMenu.printDivider();
        System.out.printf("%-4s | %-21s | %-10s | %-9s | %s%n",
                "ID", "PROJECT NAME", "TYPE", "TEAM SIZE", "BUDGET");
        ConsoleMenu.printDivider();

        if (results.length == 0) {
            System.out.println("No projects found.");
        } else {
            for (Project p : results) {
                p.displayProject();
                ConsoleMenu.printDivider();
            }
        }

        String projectId = ValidationUtils.readNonEmptyString(
                scanner, "\nEnter project ID to view details (or 0 to return): ");
        if (!projectId.equals("0")) {
            viewProjectDetails(projectId);
        }
    }

    /** Shows one project's full details and tasks, looping its own submenu until "back". */
    private static void viewProjectDetails(String projectId) {
        Project project;
        try {
            project = projectService.findProject(projectId);
        } catch (ProjectNotFoundException e) {
            ConsoleMenu.printError(e);
            ConsoleMenu.pause(scanner);
            return;
        }

        boolean viewing = true;
        while (viewing) {
            ConsoleMenu.printHeader("PROJECT DETAILS: " + project.getId());
            System.out.println("\nProject Name: " + project.getName());
            System.out.println("Type: " + project.getProjectDetails());
            System.out.println("Team Size: " + project.getTeamSize());
            System.out.println("Budget: $" + String.format("%,.2f", project.getBudget()));

            System.out.println("\nAssociated Tasks:");
            ConsoleMenu.printDivider();
            System.out.printf("%-4s | %-19s | %-12s | %s%n", "ID", "TASK NAME", "STATUS", "ASSIGNED TO");
            ConsoleMenu.printDivider();

            Task[] tasks = project.getTasks();
            if (tasks.length == 0) {
                System.out.println("No tasks recorded for this project.");
            } else {
                for (Task t : tasks) {
                    t.displayTask();
                }
            }
            ConsoleMenu.printDivider();
            System.out.printf("Completion Rate: %.2f%%%n", project.getCompletionPercentage());

            System.out.println("\nOptions:");
            System.out.println("1. Add New Task");
            System.out.println("2. Update Task Status" + (currentUser.canModify() ? "" : " (Admin only)"));
            System.out.println("3. Remove Task" + (currentUser.canModify() ? "" : " (Admin only)"));
            System.out.println("4. Back to Main Menu");

            int choice = ValidationUtils.readInt(scanner, "\nEnter your choice: ");
            switch (choice) {
                case 1:
                    addTaskToProject(project);
                    break;
                case 2:
                    updateTaskStatus(project);
                    break;
                case 3:
                    removeTaskFromProject(project);
                    break;
                case 4:
                    viewing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ================= Epic 2: Task Operations =================

    /**
     * Entry point for "Manage Tasks" from the main menu: pick a project by ID
     * first. The not-found case is handled once, inside viewProjectDetails()
     * itself, rather than being checked redundantly here too.
     */
    private static void manageTasks() {
        String projectId = ValidationUtils.readNonEmptyString(scanner, "\nEnter assigned project ID: ");
        viewProjectDetails(projectId);
    }

    /** Prompts for task details (rejecting duplicate names) and adds it to the given project. */
    private static void addTaskToProject(Project project) {
        ConsoleMenu.printHeader("ADD NEW TASK");
        String name = ValidationUtils.readNonEmptyString(scanner, "\nEnter task name: ");

        try {
            if (project.hasTaskNamed(name)) {
                throw new InvalidInputException("A task named \"" + name + "\" already exists in this project.");
            }
        } catch (InvalidInputException e) {
            ConsoleMenu.printError(e);
            ConsoleMenu.pause(scanner);
            return;
        }

        TaskStatus status = ValidationUtils.readValidStatus(
                scanner, "Enter initial status (Pending/In Progress/Completed): ");
        User assignedTo = selectUser();

        Task task = new Task(name, status, assignedTo);
        project.addTask(task);
        System.out.println("\n✓ Task \"" + name + "\" added successfully to Project " + project.getId() + "!");
        ConsoleMenu.pause(scanner);
    }

    /** Prompts for a user ID and returns the matching User, re-prompting until valid. */
    private static User selectUser() {
        System.out.println("\nAvailable Users:");
        for (User user : userService.getAllUsers()) {
            System.out.println(user.getId() + " - " + user.getName() + " (" + user.getRole() + ")");
        }
        while (true) {
            String input = ValidationUtils.readNonEmptyString(scanner, "Assign to user ID: ");
            for (User user : userService.getAllUsers()) {
                if (user.getId().equalsIgnoreCase(input)) {
                    return user;
                }
            }
            System.out.println("❌ Error: No user found with ID \"" + input + "\".");
        }
    }

    /** Admin-only: looks up a task by ID within this project and updates its status. */
    private static void updateTaskStatus(Project project) {
        // ROLE-BASED ACCESS (Epic 3): only Admin users may update task status.
        if (!currentUser.canModify()) {
            System.out.println("❌ Access denied: only Admin users can update task status.");
            ConsoleMenu.pause(scanner);
            return;
        }
        String taskId = ValidationUtils.readNonEmptyString(scanner, "\nEnter task ID: ");
        try {
            Task task = taskService.getTaskInProject(project, taskId);
            TaskStatus newStatus = ValidationUtils.readValidStatus(scanner, "Enter new status: ");
            task.setStatus(newStatus);
            System.out.println("\n✓ Task \"" + task.getName() + "\" marked as " + newStatus.getLabel() + ".");
        } catch (TaskNotFoundException e) {
            ConsoleMenu.printError(e);
            System.out.println("Operation aborted. Returning to task menu...");
        }
        ConsoleMenu.pause(scanner);
    }

    /** Admin-only: removes a task by ID from this specific project. */
    private static void removeTaskFromProject(Project project) {
        // ROLE-BASED ACCESS (Epic 3): only Admin users may delete.
        if (!currentUser.canModify()) {
            System.out.println("❌ Access denied: only Admin users can remove tasks.");
            ConsoleMenu.pause(scanner);
            return;
        }
        String taskId = ValidationUtils.readNonEmptyString(scanner, "\nEnter task ID to remove: ");
        try {
            taskService.removeTaskFromProject(project, taskId);
            System.out.println("\n✓ Task removed successfully.");
        } catch (TaskNotFoundException e) {
            ConsoleMenu.printError(e);
        }
        ConsoleMenu.pause(scanner);
    }

    // ================= Epic 3: User Management =================

    /** "Manage Users" submenu: switch the active user, or register a new one. */
    private static void manageUsers() {
        ConsoleMenu.printHeader("MANAGE USERS");
        System.out.println("\nOptions:");
        System.out.println("1. Switch User");
        System.out.println("2. Add New User");
        System.out.println("3. Back to Main Menu");

        int choice = ValidationUtils.readInt(scanner, "\nEnter your choice: ");
        switch (choice) {
            case 1:
                switchUser();
                break;
            case 2:
                addUser();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /** Lets the user pick which registered user is "logged in." */
    private static void switchUser() {
        User[] users = userService.getAllUsers();
        System.out.println("\nAvailable Users:");
        for (int i = 0; i < users.length; i++) {
            System.out.println((i + 1) + ". " + users[i].getName() + " (" + users[i].getRole() + ")");
        }
        int choice = ValidationUtils.readInt(scanner, "\nSelect user: ");
        if (choice >= 1 && choice <= users.length) {
            currentUser = users[choice - 1];
            System.out.println("\n✓ Switched to " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        } else {
            System.out.println("Invalid selection - user unchanged.");
        }
        ConsoleMenu.pause(scanner);
    }

    /**
     * Prompts for a new user's role, name, and email, and registers them.
     * Role is chosen from a fixed menu so it can never itself be invalid;
     * email is free text and goes through User's own validation, re-prompting
     * in place via InvalidUserDataException until it looks like a real email.
     */
    private static void addUser() {
        ConsoleMenu.printHeader("ADD NEW USER");
        System.out.println("\nRole:");
        System.out.println("1. Admin");
        System.out.println("2. Regular");
        int roleChoice;
        while (true) {
            roleChoice = ValidationUtils.readInt(scanner, "Select role (1-2): ");
            if (roleChoice == 1 || roleChoice == 2) {
                break;
            }
            System.out.println("❌ Error: Please enter 1 or 2.");
        }

        String name = ValidationUtils.readNonEmptyString(scanner, "Enter user name: ");

        while (true) {
            String email = ValidationUtils.readNonEmptyString(scanner, "Enter user email: ");
            try {
                User newUser = (roleChoice == 1) ? new AdminUser(name, email) : new RegularUser(name, email);
                userService.addUser(newUser);
                System.out.println("\n✓ User \"" + name + "\" created successfully. (ID: " + newUser.getId() + ")");
                break;
            } catch (InvalidUserDataException e) {
                ConsoleMenu.printError(e);
            }
        }
        ConsoleMenu.pause(scanner);
    }

    // ================= Epic 4: Status Processing & Reporting =================

    /** "View Status Reports" submenu: the full table, or one project's completion on demand. */
    private static void manageReports() {
        ConsoleMenu.printHeader("STATUS REPORTS");
        System.out.println("\nOptions:");
        System.out.println("1. View Full Status Report");
        System.out.println("2. Check Single Project Completion");
        System.out.println("3. Back to Main Menu");

        int choice = ValidationUtils.readInt(scanner, "\nEnter your choice: ");
        switch (choice) {
            case 1:
                reportService.printStatusReport();
                ConsoleMenu.pause(scanner);
                break;
            case 2:
                checkSingleProjectCompletion();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Looks up one project and reports its completion percentage, distinct
     * from the aggregate table: a project with zero tasks is treated as
     * exceptional here (EmptyProjectException) rather than a valid 0.00%.
     */
    private static void checkSingleProjectCompletion() {
        ConsoleMenu.printHeader("CALCULATE PROJECT COMPLETION");
        String projectId = ValidationUtils.readNonEmptyString(scanner, "\nEnter project ID: ");
        try {
            Project project = projectService.findProject(projectId);
            double percentage = reportService.checkCompletion(project);
            System.out.printf("%n✓ Project \"%s\" is %.2f%% complete.%n", project.getName(), percentage);
        } catch (ProjectNotFoundException | EmptyProjectException e) {
            ConsoleMenu.printError(e);
        }
        ConsoleMenu.pause(scanner);
    }

    // ================= Sample data seeding =================

    /** Populates 2 sample users and 5 sample projects with tasks, for demo purposes. */
    private static void seedSampleData() throws InvalidProjectDataException, InvalidUserDataException {
        User alice = new AdminUser("Alice Johnson", "alice.johnson@amalitech.dev");
        User bob = new RegularUser("Bob Kariuki", "bob.kariuki@amalitech.dev");
        userService.addUser(alice);
        userService.addUser(bob);
        currentUser = alice;

        Project p1 = new SoftwareProject("Alpha Tracker", "Task tracking app for startups", 15000.00, 5);
        p1.addTask(new Task("Design Database", TaskStatus.COMPLETED, bob));
        p1.addTask(new Task("Implement API", TaskStatus.IN_PROGRESS, alice));
        p1.addTask(new Task("Write Unit Tests", TaskStatus.PENDING));
        projectService.addProject(p1);

        Project p2 = new HardwareProject("IoT Sensor Kit", "Sensor prototype for smart devices", 10000.00, 3);
        p2.addTask(new Task("Design Circuit", TaskStatus.COMPLETED, alice));
        p2.addTask(new Task("Assemble Prototype", TaskStatus.PENDING));
        projectService.addProject(p2);

        Project p3 = new SoftwareProject("Beta Dashboard", "Analytics dashboard for internal use", 22000.00, 6);
        p3.addTask(new Task("Design Wireframes", TaskStatus.COMPLETED));
        p3.addTask(new Task("Build Charts Module", TaskStatus.COMPLETED));
        p3.addTask(new Task("Integrate Data Feed", TaskStatus.IN_PROGRESS));
        p3.addTask(new Task("User Acceptance Testing", TaskStatus.PENDING));
        projectService.addProject(p3);

        Project p4 = new HardwareProject("SmartLock Device", "Bluetooth-enabled smart door lock", 18000.00, 4);
        p4.addTask(new Task("Firmware Development", TaskStatus.IN_PROGRESS));
        projectService.addProject(p4);

        Project p5 = new SoftwareProject("Gamma CRM", "Customer relationship management tool", 30000.00, 8);
        p5.addTask(new Task("Requirements Gathering", TaskStatus.COMPLETED));
        p5.addTask(new Task("Database Schema Design", TaskStatus.COMPLETED));
        projectService.addProject(p5);
    }
}
