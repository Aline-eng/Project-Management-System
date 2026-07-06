import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import models.User;

/**
 * Application entry point.
 *
 * PHASE 1 + PHASE 2 SCOPE ONLY: this class currently just proves that the
 * Project/User hierarchies AND the Task/completion logic work correctly by
 * constructing sample objects and printing them. The real menu-driven
 * console UI replaces this scaffold in Phase 3.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Phase 1: Foundation Setup Test ===\n");

        // POLYMORPHISM: both variables are declared as the PARENT type (Project),
        // but each holds a different CHILD object (SoftwareProject / HardwareProject).
        Project p1 = new SoftwareProject(
                "Alpha Tracker", "Task tracking app for startups", 15000.00, 5, "Java");
        Project p2 = new HardwareProject(
                "IoT Sensor Kit", "Sensor prototype for smart devices", 10000.00, 3, "Sensors");

        System.out.println("Sample Projects:");
        p1.displayProject();
        p2.displayProject();

        // POLYMORPHISM again: both declared as parent type User.
        User u1 = new AdminUser("Alice Johnson", "alice.johnson@amalitech.dev");
        User u2 = new RegularUser("Bob Kariuki", "bob.kariuki@amalitech.dev");

        System.out.println("\nSample Users:");
        u1.displayUserInfo();
        System.out.println();
        u2.displayUserInfo();

        System.out.println("\n=== Phase 1 objects created and displayed successfully ===");

        System.out.println("\n=== Phase 2: Task & Completion Logic Test ===\n");

        // Add tasks to p1 (Alpha Tracker) - mirrors the lab's example screenshot:
        // Design Database (Completed), Implement API (In Progress), Write Unit Tests (Pending)
        p1.addTask(new Task("Design Database", TaskStatus.COMPLETED));
        p1.addTask(new Task("Implement API", TaskStatus.IN_PROGRESS));
        p1.addTask(new Task("Write Unit Tests", TaskStatus.PENDING));

        System.out.println("Tasks for " + p1.getId() + " - " + p1.getName() + ":");
        for (Task t : p1.getTasks()) {
            t.displayTask();
        }
        System.out.printf("Completion Rate: %.2f%%%n", p1.getCompletionPercentage());

        // Demonstrate updating a task's status by ID (US-2.2), then
        // recalculating completion (proves the array mutation is real,
        // not a copy that silently gets discarded).
        Task apiTask = p1.findTask("TSK002");
        if (apiTask != null) {
            apiTask.setStatus(TaskStatus.COMPLETED);
            System.out.println("\n\u2713 Task \"" + apiTask.getName() + "\" marked as Completed.");
        }
        System.out.printf("Updated Completion Rate: %.2f%%%n", p1.getCompletionPercentage());

        // Edge case required by US-4.1: a project with zero tasks must not
        // crash or show NaN - it should cleanly report 0.00%.
        System.out.println("\nEdge case - zero-task project (" + p2.getId() + "):");
        System.out.printf("Completion Rate: %.2f%%%n", p2.getCompletionPercentage());

        System.out.println("\n=== Phase 2 task/completion logic verified successfully ===");
    }
}
