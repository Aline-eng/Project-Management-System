package models;

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
     * Returns the project's type label (e.g. "Software", "Hardware"), backed
     * by the ProjectType enum. Abstract - each subclass returns its own
     * constant, which is the polymorphism this method exists to demonstrate.
     */
    public abstract String getProjectDetails();

    /**
     * Concrete (non-abstract) method - identical for every project type,
     * so it lives here ONCE and is simply inherited by every subclass,
     * instead of being duplicated in SoftwareProject and HardwareProject.
     *
     * OOP CONCEPT - POLYMORPHISM:
     *   This method calls getProjectDetails() on "this" - even though
     *   displayProject() is written here in the parent class, Java calls the
     *   OVERRIDDEN version from whichever concrete subclass the actual
     *   object is. This is runtime (dynamic) polymorphism.
     */
    public void displayProject() {
        System.out.printf("%-4s | %-21s | %-10s | %-9d | $%,.2f%n",
                id, name, getProjectDetails(), teamSize, budget);
        System.out.println("     | Description: " + description);
    }

    // ---- Task management (COMPOSITION in action) ----

    /**
     * Adds a task to this project's array.
     * O(1) - simply places the task at the next free index.
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
     * DSA CONCEPT - LINEAR SEARCH: scans this project's task array from the
     * start until a matching ID is found. O(n) where n is this project's
     * own task count (not the whole system) - deliberately scoped, since a
     * task only needs to be searched for within its owning project here.
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
     * Removes a task by ID, shifting later elements left by one to keep the
     * array dense (no holes) - required so taskCount always matches the
     * number of real, non-null entries at the front of the array.
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

    public Task[] getTasks() {
        // Return only the "live" portion of the array (0..taskCount-1) as a
        // right-sized copy, so callers can never see stale/null trailing slots
        // or accidentally corrupt this project's internal array reference.
        Task[] result = new Task[taskCount];
        System.arraycopy(tasks, 0, result, 0, taskCount);
        return result;
    }

    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Counts how many of this project's tasks are completed.
     * OOP CONCEPT - POLYMORPHISM/INTERFACE USE: calls isCompleted() through
     * the Completable contract - Project doesn't need to know anything about
     * TaskStatus internals to do this counting.
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
     * Calculates the percentage of this project's tasks that are completed,
     * rounded to 2 decimal places as required by US-4.1.
     *
     * EDGE CASE HANDLED: a project with zero tasks would otherwise divide by
     * zero (producing NaN in Java for a double 0.0/0.0) - we explicitly
     * return 0.0 instead, since "0 of 0 tasks complete" is not meaningfully
     * "100% done" nor an error state; it is simply "nothing to report yet".
     */
    public double getCompletionPercentage() {
        if (taskCount == 0) {
            return 0.0;
        }
        double rawPercentage = (getCompletedTaskCount() * 100.0) / taskCount;
        // Round to 2 decimal places: multiply, round to nearest whole number,
        // then divide back down - a common, simple rounding technique that
        // avoids pulling in java.math.BigDecimal for a case this simple.
        return Math.round(rawPercentage * 100.0) / 100.0;
    }
}
