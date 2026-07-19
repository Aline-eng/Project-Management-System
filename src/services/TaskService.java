package services;

import models.project.Project;
import models.Task;
import utils.exceptions.TaskNotFoundException;

/**
 * Task operations scoped to a specific project. Every real use in this app
 * already knows which project it's operating within (the user is always
 * inside that project's details screen when managing its tasks), so lookups
 * take the Project explicitly instead of searching the whole catalog - this
 * also guarantees a task can only be updated/removed through the project it
 * actually belongs to, not through an unrelated project's screen.
 */
public class TaskService {
    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Finds a task by ID within the given project.
     *
     * @return the matching task
     * @throws TaskNotFoundException if no task with this ID exists in this project
     */
    public Task getTaskInProject(Project project, String taskId) throws TaskNotFoundException {
        Task task = project.findTask(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
        }
        return task;
    }

    /**
     * Removes a task by ID from the given project.
     *
     * @throws TaskNotFoundException if no task with this ID exists in this project
     */
    public void removeTaskFromProject(Project project, String taskId) throws TaskNotFoundException {
        getTaskInProject(project, taskId);
        project.removeTask(taskId);
    }
}
