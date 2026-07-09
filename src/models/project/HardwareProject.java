package models.project;

import enums.ProjectType;

public class HardwareProject extends Project {

    public HardwareProject(String name, String description, double budget, int teamSize) {
        super(name, description, budget, teamSize);
    }

    @Override
    public String getProjectDetails() {
        return ProjectType.HARDWARE.getLabel();
    }
}
