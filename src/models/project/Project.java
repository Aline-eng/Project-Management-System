package models.project;

import enums.ProjectType;
import models.Task;

/**
 * Abstract base class for a project. Owns its own array of tasks and the
 * invariants around them (uniqueness, capacity, completion accounting).
 * Concrete subclasses only need to say what type of project they are.
 */
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

    /**
     * Returns this project's type. Each concrete subclass returns its own
     * {@link ProjectType} constant - this is the polymorphic hook Project's
     * own methods (like {@link #displayProject()}) rely on without ever
     * needing to know which concrete subclass they're working with.
     */
    public abstract ProjectType getProjectDetails();

    /**
     * Prints one row of a project listing table. Written once here and
     * inherited by every subclass; the type column differs per subclass
     * purely because {@link #getProjectDetails()} is polymorphic.
     */
    public void displayProject() {
        System.out.printf("%-4s | %-21s | %-10s | %-9d | $%,.2f%n",
                id, name, getProjectDetails(), teamSize, budget);
        System.out.println("     | Description: " + description);
    }

    /**
     * Checks whether this project already has a task with the given name
     * (case-insensitive). This is this project's own invariant to enforce -
     * task names only need to be unique within a single project - so the
     * check lives here rather than in the UI layer.
     *
     * @param name the task name to check
     * @return true if a task with this name already exists in this project
     */
    public boolean hasTaskNamed(String name) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a task to this project's array.
     *
     * @return true if added; false if this project's task storage is full
     */
    public boolean addTask(Task task) {
        if (taskCount >= tasks.length) {
            System.out.println("Task storage for this project is full.");
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    /**
     * Finds a task belonging to this project by ID.
     *
     * @return the matching task, or null if no task with this ID exists here
     */
    public Task findTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId().equalsIgnoreCase(taskId)) {
                return tasks[i];
            }
        }
        return null;
    }

    /**
     * Removes a task from this project by ID, shifting later elements left
     * to keep the array dense.
     *
     * @return true if a matching task was found and removed; false otherwise
     */
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

    /**
     * Returns a defensive copy of this project's tasks, sized to exactly
     * how many are actually stored. Callers can never see stale/null
     * trailing slots, or mutate this project's internal array directly.
     */
    public Task[] getTasks() {
        Task[] result = new Task[taskCount];
        System.arraycopy(tasks, 0, result, 0, taskCount);
        return result;
    }

    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Counts how many of this project's tasks are completed, via the
     * {@link interfaces.Completable} contract - no knowledge of TaskStatus
     * internals needed here.
     */
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

    /**
     * Percentage of this project's tasks that are completed, rounded to 2
     * decimal places. A project with zero tasks returns 0.0 rather than
     * dividing by zero - "nothing to report yet," not an error state.
     */
    public double getCompletionPercentage() {
        if (taskCount == 0) {
            return 0.0;
        }
        double rawPercentage = (getCompletedTaskCount() * 100.0) / taskCount;
        return Math.round(rawPercentage * 100.0) / 100.0;
    }
}
