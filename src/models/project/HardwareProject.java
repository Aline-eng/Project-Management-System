package models.project;

import enums.ProjectType;

/** A hardware development project. */
public class HardwareProject extends Project {

    public HardwareProject(String name, String description, double budget, int teamSize) {
        super(name, description, budget, teamSize);
    }

    @Override
    public ProjectType getProjectDetails() {
        return ProjectType.HARDWARE;
    }
}
