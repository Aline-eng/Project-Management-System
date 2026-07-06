import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.User;


public class Main {

    public static void main(String[] args) {
        System.out.println("=== Phase 1: Foundation Setup Test ===\n");

        Project p1 = new SoftwareProject(
                "Alpha Tracker", "Task tracking app for startups", 15000.00, 5, "Java");
        Project p2 = new HardwareProject(
                "IoT Sensor Kit", "Sensor prototype for smart devices", 10000.00, 3, "Sensors");

        System.out.println("Sample Projects:");
        p1.displayProject();
        p2.displayProject();

        User u1 = new AdminUser("Alice Johnson", "alice.johnson@amalitech.dev");
        User u2 = new RegularUser("Bob Kariuki", "bob.kariuki@amalitech.dev");

        System.out.println("\nSample Users:");
        u1.displayUserInfo();
        System.out.println();
        u2.displayUserInfo();

        System.out.println("\n=== Phase 1 objects created and displayed successfully ===");
    }
}
