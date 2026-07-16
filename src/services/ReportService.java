package services;

import models.project.Project;
import models.StatusReport;
import utils.ConsoleMenu;

/**
 * Builds and prints completion reports aggregated across every project.
 */
public class ReportService {
    private final ProjectService projectService;

    public ReportService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** @return one StatusReport row per project currently in the catalog */
    public StatusReport[] generateReports() {
        Project[] projects = projectService.getAllProjects();
        StatusReport[] reports = new StatusReport[projects.length];
        for (int i = 0; i < projects.length; i++) {
            Project p = projects[i];
            reports[i] = new StatusReport(
                    p.getId(), p.getName(), p.getTaskCount(),
                    p.getCompletedTaskCount(), p.getCompletionPercentage());
        }
        return reports;
    }
    /** @return the average completion percentage across all projects, or 0.0 if there are none */
    public double calculateAverageCompletion() {
        Project[] projects = projectService.getAllProjects();
        if (projects.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project p : projects) {
            sum += p.getCompletionPercentage();
        }
        double average = sum / projects.length;
        return Math.round(average * 100.0) / 100.0;
    }
    public void printStatusReport() {
        ConsoleMenu.printHeader("PROJECT STATUS REPORT");
        ConsoleMenu.printDivider();
        System.out.printf("%-10s | %-18s | %5s | %9s | %s%n",
                "PROJECT ID", "PROJECT NAME", "TASKS", "COMPLETED", "PROGRESS (%)");
        ConsoleMenu.printDivider();

        StatusReport[] reports = generateReports();
        if (reports.length == 0) {
            System.out.println("No projects available yet.");
        } else {
            for (StatusReport report : reports) {
                report.displayRow();
            }
        }

        ConsoleMenu.printDivider();
        System.out.printf("AVERAGE COMPLETION: %.2f%%%n", calculateAverageCompletion());
        ConsoleMenu.printDivider();
    }
}
