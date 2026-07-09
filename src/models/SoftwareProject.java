package models;

/**
 * Concrete subclass of Project representing a software development project.
 *
 * OOP CONCEPT - INHERITANCE:
 *   "extends Project" reuses id, name, description, budget, teamSize and all
 *   getters/setters/displayProject() from Project without rewriting them.
 *
 * OOP CONCEPT - POLYMORPHISM (overriding):
 *   Supplies its own getProjectDetails(), returning the Software type label.
 */
public class SoftwareProject extends Project {

    public SoftwareProject(String name, String description, double budget, int teamSize) {
        super(name, description, budget, teamSize);
    }

    @Override
    public String getProjectDetails() {
        return ProjectType.SOFTWARE.getLabel();
    }
}
