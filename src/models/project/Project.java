package models.project;

import models.Task;

public abstract class Project {

    private String id;
    private String name;
    private String description;
    private double budget;
    private int teamSize;

    private Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS_PER_PROJECT = 50;

    private static int projectCounter = 0;

    protected Project(String name, String description, double budget, int teamSize) {
        projectCounter++;
        this.id = String.format("PRJ%03d", projectCounter);
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.teamSize = teamSize;
        this.tasks = new Task[MAX_TASKS_PER_PROJECT];
        this.taskCount = 0;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public abstract String getProjectDetails();

    // Runtime polymorphism: calls the overridden getProjectDetails() in the subclass
    public void displayProject() {
        System.out.printf("%-4s | %-21s | %-10s | %-9d | $%,.2f%n",
                id, name, getProjectDetails(), teamSize, budget);
        System.out.println("     | Description: " + description);
    }

    
    public boolean addTask(Task task) {
        if (taskCount >= tasks.length) {
            System.out.println("Task storage for this project is full.");
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    public Task findTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId().equalsIgnoreCase(taskId)) {
                return tasks[i];
            }
        }
        return null;
    }

    public boolean removeTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId().equalsIgnoreCase(taskId)) {
                for (int j = i; j < taskCount - 1; j++) {
                    tasks[j] = tasks[j + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;
                return true;
            }
        }
        return false;
    }

    public Task[] getTasks() {
        Task[] result = new Task[taskCount];
        System.arraycopy(tasks, 0, result, 0, taskCount);
        return result;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int getCompletedTaskCount() {
        int completed = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].isCompleted()) {
                completed++;
            }
        }
        return completed;
    }

    public int getPendingTaskCount() {
        return taskCount - getCompletedTaskCount();
    }

    public double getCompletionPercentage() {
        if (taskCount == 0) {
            return 0.0;
        }
        double rawPercentage = (getCompletedTaskCount() * 100.0) / taskCount;
        return Math.round(rawPercentage * 100.0) / 100.0;
    }
}
