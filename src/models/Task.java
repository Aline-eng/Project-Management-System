package models;

import enums.TaskStatus;
import interfaces.Completable;
import models.user.User;

public class Task implements Completable {

    private String id;
    private String name;
    private TaskStatus status;
    private User assignedTo;

    private static int taskCounter = 0;

    public Task(String name, TaskStatus status) {
        this(name, status, null);
    }

    public Task(String name, TaskStatus status, User assignedTo) {
        taskCounter++;
        this.id = String.format("TSK%03d", taskCounter);
        this.name = name;
        this.status = status;
        this.assignedTo = assignedTo;
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

    public User getAssignedTo() {
        return assignedTo;
    }

    @Override
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public void displayTask() {
        String assignedName = (assignedTo != null) ? assignedTo.getName() : "Unassigned";
        System.out.printf("%-4s | %-19s | %-12s | %s%n", id, name, status.getLabel(), assignedName);
    }
}
