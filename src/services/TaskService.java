package services;

import models.Project;
import models.Task;

public class TaskService {
    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public Task findTaskAnywhere(String taskId) {
        for (Project project : projectService.getAllProjects()) {
            Task task = project.findTask(taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }
    public Project findProjectOwningTask(String taskId) {
        for (Project project : projectService.getAllProjects()) {
            if (project.findTask(taskId) != null) {
                return project;
            }
        }
        return null;
    }

    public boolean removeTask(String taskId) {
        Project owner = findProjectOwningTask(taskId);
        if (owner == null) {
            return false;
        }
        return owner.removeTask(taskId);
    }
}
