package models;

/**
 * Abstract base class representing a generic Project in the system.
 *
 * OOP CONCEPT - ABSTRACTION:
 *   Project can never be instantiated directly ("new Project(...)" is illegal).
 *   It defines the data and behavior every project shares, and forces every
 *   concrete subclass to implement getProjectDetails() in its own way.
 *
 * OOP CONCEPT - ENCAPSULATION:
 *   All fields are private; access is only possible through the public
 *   getters/setters below, protecting the object's internal state.
 */
public abstract class Project {

    // ENCAPSULATION: private fields - no outside class can touch these directly
    private String id;
    private String name;
    private String description;
    private double budget;
    private int teamSize;

    // STATIC FIELD: shared by every Project object (of any subtype), used to
    // auto-generate unique, sequential project IDs: PRJ001, PRJ002, PRJ003...
    private static int projectCounter = 0;

    protected Project(String name, String description, double budget, int teamSize) {
        projectCounter++;
        this.id = String.format("PRJ%03d", projectCounter);
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.teamSize = teamSize;
    }

    // ---- Getters and setters (ENCAPSULATION) ----

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    /**
     * ABSTRACTION: each project type (Software/Hardware) reports its own
     * type-specific detail line. No default implementation exists here -
     * every subclass MUST provide one.
     */
    public abstract String getProjectDetails();

    /**
     * Returns the project "type" label used throughout the console UI
     * (e.g. "Software", "Hardware"). Also abstract - defined per subclass.
     */
    public abstract String getProjectType();

    /**
     * Concrete (non-abstract) method - identical for every project type,
     * so it lives here ONCE and is simply inherited by every subclass,
     * instead of being duplicated in SoftwareProject and HardwareProject.
     *
     * OOP CONCEPT - POLYMORPHISM:
     *   This method calls getProjectDetails() and getProjectType() on "this" -
     *   even though displayProject() is written here in the parent class,
     *   Java calls the OVERRIDDEN version from whichever concrete subclass
     *   the actual object is. This is runtime (dynamic) polymorphism.
     */
    public void displayProject() {
        System.out.printf("%-4s | %-21s | %-10s | %-9d | $%,.2f%n",
                id, name, getProjectType(), teamSize, budget);
        System.out.println("     | Description: " + description);
        System.out.println("     | " + getProjectDetails());
    }
}
