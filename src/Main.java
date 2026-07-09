import java.util.Scanner;

import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import models.User;
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import utils.ConsoleMenu;
import utils.ValidationUtils;

/**
 * Application entry point - the full menu-driven console UI, wiring together
 * every model and service built in Phases 1 and 2.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProjectService projectService = new ProjectService();
    private static final TaskService taskService = new TaskService(projectService);
    private static final ReportService reportService = new ReportService(projectService);

    // Simple in-memory user list to support "Switch User" (Epic 3).
    private static final User[] users = new User[2];
    private static User currentUser;

    public static void main(String[] args) {
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
            System.out.println("4. Switch User");
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
                    reportService.printStatusReport();
                    ConsoleMenu.pause(scanner);
                    break;
                case 4:
                    switchUser();
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
        if (typeChoice == 1) {
            project = new SoftwareProject(name, description, budget, teamSize);
        } else if (typeChoice == 2) {
            project = new HardwareProject(name, description, budget, teamSize);
        } else {
            System.out.println("Invalid project type selected.");
            return;
        }

        projectService.addProject(project);
        System.out.println("\n✓ Project successfully created. (ID: " + project.getId() + ")");
        ConsoleMenu.pause(scanner);
    }

    private static void viewProjectCatalog() {
        ConsoleMenu.printHeader("PROJECT CATALOG");
        System.out.println("\nFilter Options:");
        System.out.println("1. View All Projects (" + projectService.getProjectCount() + ")");
        System.out.println("2. Software Projects Only");
        System.out.println("3. Hardware Projects Only");
        System.out.println("4. Search by Budget Range");

        int filterChoice = ValidationUtils.readInt(scanner, "\nEnter filter choice: ");
        Project[] results;

        switch (filterChoice) {
            case 2:
                results = projectService.getProjectsByType("Software");
                break;
            case 3:
                results = projectService.getProjectsByType("Hardware");
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

    private static void viewProjectDetails(String projectId) {
        Project project = projectService.findProject(projectId);
        if (project == null) {
            System.out.println("❌ Error: Invalid input. Please enter a valid project ID (e.g., PRJ001).");
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

    private static void manageTasks() {
        String projectId = ValidationUtils.readNonEmptyString(scanner, "\nEnter assigned project ID: ");
        Project project = projectService.findProject(projectId);
        if (project == null) {
            System.out.println("❌ Error: Invalid input. Please enter a valid numeric or prefixed ID (e.g., PRJ001).");
            ConsoleMenu.pause(scanner);
            return;
        }
        viewProjectDetails(projectId);
    }

    private static void addTaskToProject(Project project) {
        ConsoleMenu.printHeader("ADD NEW TASK");
        String name = ValidationUtils.readNonEmptyString(scanner, "\nEnter task name: ");

        // Prevent duplicate task names within the same project (US-2.1 requirement).
        for (Task existing : project.getTasks()) {
            if (existing.getName().equalsIgnoreCase(name)) {
                System.out.println("❌ Error: A task with this name already exists in this project.");
                ConsoleMenu.pause(scanner);
                return;
            }
        }

        TaskStatus status = ValidationUtils.readValidStatus(
                scanner, "Enter initial status (Pending/In Progress/Completed): ");
        User assignedTo = selectUser();

        Task task = new Task(name, status, assignedTo);
        project.addTask(task);
        System.out.println("\n✓ Task \"" + name + "\" added successfully to Project " + project.getId() + "!");
        ConsoleMenu.pause(scanner);
    }

    /**
     * Prompts for a user ID and returns the matching User (Feature 3: "Assign
     * users to projects or tasks"). Same linear-search-and-validate pattern
     * used everywhere else IDs are looked up (ProjectService, Project).
     */
    private static User selectUser() {
        System.out.println("\nAvailable Users:");
        for (User user : users) {
            System.out.println(user.getId() + " - " + user.getName() + " (" + user.getRole() + ")");
        }
        while (true) {
            String input = ValidationUtils.readNonEmptyString(scanner, "Assign to user ID: ");
            for (User user : users) {
                if (user.getId().equalsIgnoreCase(input)) {
                    return user;
                }
            }
            System.out.println("❌ Error: No user found with ID \"" + input + "\".");
        }
    }

    private static void updateTaskStatus(Project project) {
        // ROLE-BASED ACCESS (Epic 3): only Admin users may update task status.
        if (!currentUser.canModify()) {
            System.out.println("❌ Access denied: only Admin users can update task status.");
            ConsoleMenu.pause(scanner);
            return;
        }
        String taskId = ValidationUtils.readNonEmptyString(scanner, "\nEnter task ID: ");
        // Routed through TaskService (not project.findTask directly) - demonstrates
        // the service layer doing real work: task IDs are unique system-wide, so
        // TaskService can locate the task without needing to trust which project
        // the caller thinks it belongs to.
        Task task = taskService.findTaskAnywhere(taskId);
        if (task == null || !project.getId().equals(taskService.findProjectOwningTask(taskId).getId())) {
            System.out.println("❌ Error: Task not found in this project.");
            ConsoleMenu.pause(scanner);
            return;
        }
        TaskStatus newStatus = ValidationUtils.readValidStatus(scanner, "Enter new status: ");
        task.setStatus(newStatus);
        System.out.println("\n✓ Task \"" + task.getName() + "\" marked as " + newStatus.getLabel() + ".");
        ConsoleMenu.pause(scanner);
    }

    private static void removeTaskFromProject(Project project) {
        // ROLE-BASED ACCESS (Epic 3): only Admin users may delete.
        if (!currentUser.canModify()) {
            System.out.println("❌ Access denied: only Admin users can remove tasks.");
            ConsoleMenu.pause(scanner);
            return;
        }
        String taskId = ValidationUtils.readNonEmptyString(scanner, "\nEnter task ID to remove: ");
        boolean removed = taskService.removeTask(taskId);
        if (removed) {
            System.out.println("\n✓ Task removed successfully.");
        } else {
            System.out.println("❌ Error: Task not found in this project.");
        }
        ConsoleMenu.pause(scanner);
    }

    // ================= Epic 3: User Management =================

    private static void switchUser() {
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

    // ================= Sample data seeding =================

    private static void seedSampleData() {
        users[0] = new AdminUser("Alice Johnson", "alice.johnson@amalitech.dev");
        users[1] = new RegularUser("Bob Kariuki", "bob.kariuki@amalitech.dev");
        currentUser = users[0];

        Project p1 = new SoftwareProject("Alpha Tracker", "Task tracking app for startups", 15000.00, 5);
        p1.addTask(new Task("Design Database", TaskStatus.COMPLETED, users[1]));
        p1.addTask(new Task("Implement API", TaskStatus.IN_PROGRESS, users[0]));
        p1.addTask(new Task("Write Unit Tests", TaskStatus.PENDING));
        projectService.addProject(p1);

        Project p2 = new HardwareProject("IoT Sensor Kit", "Sensor prototype for smart devices", 10000.00, 3);
        p2.addTask(new Task("Design Circuit", TaskStatus.COMPLETED, users[0]));
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