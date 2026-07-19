package models.project;

import enums.ProjectType;
import utils.exceptions.InvalidProjectDataException;

/** A software development project. */
public class SoftwareProject extends Project {

    public SoftwareProject(String name, String description, double budget, int teamSize)
            throws InvalidProjectDataException {
        super(name, description, budget, teamSize);
    }

    @Override
    public ProjectType getProjectDetails() {
        return ProjectType.SOFTWARE;
    }
}
