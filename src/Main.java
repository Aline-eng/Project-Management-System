import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.User;

/**
 * Application entry point.
 *
 * PHASE 1 SCOPE ONLY: this class currently just proves that the Project and
 * User hierarchies work correctly by constructing sample objects and printing
 * them. The real menu-driven console UI is built in Phase 3.
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
    }
}
