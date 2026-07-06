package models;

public class HardwareProject extends Project {

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
