package models;

public abstract class Project {

    private String id;
    private String name;
    private String description;
    private double budget;
    private int teamSize;

    private static int projectCounter = 0;

    protected Project(String name, String description, double budget, int teamSize) {
        projectCounter++;
        this.id = String.format("PRJ%03d", projectCounter);
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.teamSize = teamSize;
    }


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

    public abstract String getProjectDetails();

    public abstract String getProjectType();


    public void displayProject() {
        System.out.printf("%-4s | %-21s | %-10s | %-9d | $%,.2f%n",
                id, name, getProjectType(), teamSize, budget);
        System.out.println("     | Description: " + description);
        System.out.println("     | " + getProjectDetails());
    }
}
