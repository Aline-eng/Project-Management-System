package services;

import models.Project;

public class ProjectService {
    private Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    public ProjectService() {
        projects = new Project[MAX_PROJECTS];
        projectCount = 0;
    }
    public boolean addProject(Project project) {
        if (projectCount >= projects.length) {
            System.out.println("Project storage is full.");
            return false;
        }
        projects[projectCount] = project;
        projectCount++;
        return true;
    }
    public Project findProject(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getId().equalsIgnoreCase(projectId)) {
                return projects[i];
            }
        }
        return null;
    }
    public Project[] getAllProjects() {
        Project[] result = new Project[projectCount];
        System.arraycopy(projects, 0, result, 0, projectCount);
        return result;
    }
    public Project[] getProjectsByType(String type) {
        int matchCount = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectType().equalsIgnoreCase(type)) {
                matchCount++;
            }
        }
        Project[] result = new Project[matchCount];
        int idx = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectType().equalsIgnoreCase(type)) {
                result[idx++] = projects[i];
            }
        }
        return result;
    }
    public Project[] searchByBudgetRange(double min, double max) {
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
    public boolean removeProject(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getId().equalsIgnoreCase(projectId)) {
                for (int j = i; j < projectCount - 1; j++) {
                    projects[j] = projects[j + 1];
                }
                projects[projectCount - 1] = null;
                projectCount--;
                return true;
            }
        }
        return false;
    }


}
