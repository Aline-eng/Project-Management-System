package models;

/**
 * Concrete subclass of Project representing a hardware development project.
 *
 * OOP CONCEPT - INHERITANCE + POLYMORPHISM:
 *   Mirrors SoftwareProject's structure but with different data and behavior -
 *   demonstrating that the same parent method name (getProjectDetails()) can
 *   produce entirely different output depending on the actual object type.
 */
public class HardwareProject extends Project {

    // Field specific to HardwareProject only.
    private String componentCategory;

    public HardwareProject(String name, String description, double budget, int teamSize, String componentCategory) {
        super(name, description, budget, teamSize);
        this.componentCategory = componentCategory;
    }

    public String getComponentCategory() {
        return componentCategory;
    }

    public void setComponentCategory(String componentCategory) {
        this.componentCategory = componentCategory;
    }

    @Override
    public String getProjectDetails() {
        return "Component Category: " + componentCategory;
    }

    @Override
    public String getProjectType() {
        return "Hardware";
    }
}
