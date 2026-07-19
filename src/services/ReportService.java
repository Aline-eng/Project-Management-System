package services;

import models.project.Project;
import models.StatusReport;
import utils.ConsoleMenu;
import utils.exceptions.EmptyProjectException;

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
    /**
     * Checks a single project's completion percentage on demand. Unlike the
     * aggregate status report (which shows every project, including empty
     * ones, at 0.00%), this is treated as a distinct, explicit request that
     * doesn't make sense to answer for a project with no tasks defined yet.
     *
     * @throws EmptyProjectException if the project has zero tasks
     */
    public double checkCompletion(Project project) throws EmptyProjectException {
        if (project.getTaskCount() == 0) {
            throw new EmptyProjectException(
                    "Project '" + project.getName() + "' has no tasks yet - nothing to calculate.");
        }
        return project.getCompletionPercentage();
    }

    /** Prints the full aggregate status report table for every project. */
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

        System.out.println("\n ✓ Report generated successfully.");
    }
}
