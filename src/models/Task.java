package models;

import interfaces.Completable;

/**
 * Represents a single task belonging to a project.
 *
 * OOP CONCEPT - ENCAPSULATION: private fields, accessed only via getters/setters.
 * OOP CONCEPT - INTERFACE IMPLEMENTATION: "implements Completable" - Task must
 *   provide isCompleted(), which simply checks its own status field.
 */
public class Task implements Completable {

    private String id;
    private String name;
    private TaskStatus status;

    // STATIC FIELD: shared by every Task object across every project, used to
    // auto-generate unique, sequential task IDs: TSK001, TSK002, TSK003...
    // NOTE: IDs are unique system-wide (not reset per project), which makes
    // looking a task up by ID alone unambiguous later in TaskService.
    private static int taskCounter = 0;

    public Task(String name, TaskStatus status) {
        taskCounter++;
        this.id = String.format("TSK%03d", taskCounter);
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * INTERFACE METHOD IMPLEMENTATION: a task is "completed" exactly when
     * its status equals TaskStatus.COMPLETED - nothing more to it, but by
     * exposing this through the Completable contract, any future code that
     * works with "completable things" in general (not just Task) can call
     * isCompleted() without knowing about TaskStatus at all.
     */
    @Override
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    /**
     * Prints one row of a task listing table.
     */
    public void displayTask() {
        System.out.printf("%-4s | %-19s | %s%n", id, name, status.getLabel());
    }
}
