package services;

import models.project.Project;
import models.Task;

/**
 * Cross-project task operations. Task IDs are unique system-wide, so this
 * class can locate a task without the caller needing to already know which
 * project it belongs to.
 */
public class TaskService {
    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Searches every project for a task with the given ID.
     *
     * @return the matching task, or null if no task with this ID exists anywhere
     */
    public Task findTaskAnywhere(String taskId) {
        for (Project project : projectService.getAllProjects()) {
            Task task = project.findTask(taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    /**
     * @return the project that owns the task with this ID, or null if none does
     */
    public Project findProjectOwningTask(String taskId) {
        for (Project project : projectService.getAllProjects()) {
            if (project.findTask(taskId) != null) {
                return project;
            }
        }
        return null;
    }

    /**
     * @return true if a task with this ID was found (in any project) and removed
     */
    public boolean removeTask(String taskId) {
        Project owner = findProjectOwningTask(taskId);
        if (owner == null) {
            return false;
        }
        return owner.removeTask(taskId);
    }
}
