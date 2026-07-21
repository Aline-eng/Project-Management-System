package models.project;

import enums.ProjectType;
import utils.exceptions.InvalidProjectDataException;

/** A hardware development project. */
public class HardwareProject extends Project {

    /** @throws InvalidProjectDataException if budget or teamSize is not positive - see Project's constructor. */
    public HardwareProject(String name, String description, double budget, int teamSize)
            throws InvalidProjectDataException {
        super(name, description, budget, teamSize);
    }

    @Override
    public ProjectType getProjectDetails() {
        return ProjectType.HARDWARE;
    }
}
