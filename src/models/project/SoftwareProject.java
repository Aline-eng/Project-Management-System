package models.project;

import enums.ProjectType;

public class SoftwareProject extends Project {

    public SoftwareProject(String name, String description, double budget, int teamSize) {
        super(name, description, budget, teamSize);
    }

    @Override
    public String getProjectDetails() {
        return ProjectType.SOFTWARE.getLabel();
    }
}
