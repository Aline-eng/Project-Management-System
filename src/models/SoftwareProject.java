package models;

/**
 * Concrete subclass of Project representing a software development project.
 *
 * OOP CONCEPT - INHERITANCE:
 *   "extends Project" reuses id, name, description, budget, teamSize and all
 *   getters/setters/displayProject() from Project without rewriting them.
 *
 * OOP CONCEPT - POLYMORPHISM (overriding):
 *   Supplies its own getProjectDetails() and getProjectType(), each returning
 *   Software-specific information.
 */
public class SoftwareProject extends Project {

    // Field specific to SoftwareProject only - HardwareProject has no use for this,
    // which is exactly why it lives in the subclass and not in the parent Project class.
    private String primaryLanguage;

    public SoftwareProject(String name, String description, double budget, int teamSize, String primaryLanguage) {
        super(name, description, budget, teamSize);
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    @Override
    public String getProjectDetails() {
        return "Primary Language: " + primaryLanguage;
    }

    @Override
    public String getProjectType() {
        return "Software";
    }
}
