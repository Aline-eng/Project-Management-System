package models;

/**
 * Concrete subclass of Project representing a hardware development project.
 *
 * OOP CONCEPT - INHERITANCE + POLYMORPHISM:
 *   Mirrors SoftwareProject's structure but overrides getProjectDetails() to
 *   return the Hardware type label instead - same method name, different
 *   behavior depending on the actual object type.
 */
public class HardwareProject extends Project {

    public HardwareProject(String name, String description, double budget, int teamSize) {
        super(name, description, budget, teamSize);
    }

    @Override
    public String getProjectDetails() {
        return ProjectType.HARDWARE.getLabel();
    }
}
