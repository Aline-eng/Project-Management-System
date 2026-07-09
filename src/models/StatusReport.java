package models;

public class StatusReport {
    private final String projectId;
    private final String projectName;
    private final int totalTasks;
    private final int completedTasks;
    private final double completionPercentage;

    public StatusReport(String projectId, String projectName, int totalTasks,
                        int completedTasks, double completionPercentage) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.completionPercentage = completionPercentage;
    }
    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }
    public void displayRow() {
        System.out.printf("%-10s | %-18s | %5d | %9d | %9.2f%%%n",
                projectId, projectName, totalTasks, completedTasks, completionPercentage);
    }
}
