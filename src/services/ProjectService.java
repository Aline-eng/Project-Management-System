package services;

import enums.ProjectType;
import models.project.Project;
import utils.exceptions.InvalidInputException;
import utils.exceptions.ProjectNotFoundException;

/**
 * Owns the in-memory project catalog: storage, lookup, and filtering.
 */
public class ProjectService {
    private Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    public ProjectService() {
        projects = new Project[MAX_PROJECTS];
        projectCount = 0;
    }

    /**
     * @return true if added; false if project storage is full
     */
    public boolean addProject(Project project) {
        if (projectCount >= projects.length) {
            System.out.println("Project storage is full.");
            return false;
        }
        projects[projectCount] = project;
        projectCount++;
        return true;
    }

    /**
     * @return the matching project
     * @throws ProjectNotFoundException if no project with this ID exists
     */
    public Project findProject(String projectId) throws ProjectNotFoundException {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getId().equalsIgnoreCase(projectId)) {
                return projects[i];
            }
        }
        throw new ProjectNotFoundException("Project ID '" + projectId + "' does not exist.");
    }

    /**
     * @return a defensive copy of every stored project
     */
    public Project[] getAllProjects() {
        Project[] result = new Project[projectCount];
        System.arraycopy(projects, 0, result, 0, projectCount);
        return result;
    }

    /**
     * Filters projects by type using enum comparison rather than a raw
     * String, so a typo like "Softwear" is a compile error, not a silent
     * empty result.
     */
    public Project[] getProjectsByType(ProjectType type) {
        int matchCount = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectDetails() == type) {
                matchCount++;
            }
        }
        Project[] result = new Project[matchCount];
        int idx = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectDetails() == type) {
                result[idx++] = projects[i];
            }
        }
        return result;
    }
    /**
     * @return every project whose budget falls within [min, max] inclusive
     * @throws InvalidInputException if min is greater than max
     */
    public Project[] searchByBudgetRange(double min, double max) throws InvalidInputException {
        if (min > max) {
            throw new InvalidInputException(
                    "Minimum budget ($" + min + ") cannot exceed maximum budget ($" + max + ").");
        }
        int matchCount = 0;
        for (int i = 0; i < projectCount; i++) {
            double budget = projects[i].getBudget();
            if (budget >= min && budget <= max) {
                matchCount++;
            }
        }
        Project[] result = new Project[matchCount];
        int idx = 0;
        for (int i = 0; i < projectCount; i++) {
            double budget = projects[i].getBudget();
            if (budget >= min && budget <= max) {
                result[idx++] = projects[i];
            }
        }
        return result;
    }
    public int getProjectCount() {
        return projectCount;
    }
}
