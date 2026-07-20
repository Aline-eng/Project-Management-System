import enums.ProjectType;
import enums.TaskStatus;
import models.Task;
import models.project.HardwareProject;
import models.project.Project;
import models.project.SoftwareProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.exceptions.InvalidProjectDataException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Project's own construction rules and its completion-percentage
 * calculation - the aggregation logic Feature 4 of the lab asks to verify
 * (zero tasks, partial completion, full completion).
 */
class ProjectTests {

    private Project project;

    /** Runs before every @Test, so each test starts from the same clean project with no tasks yet. */
    @BeforeEach
    void setUp() throws InvalidProjectDataException {
        project = new SoftwareProject("Test Project", "A project used for testing", 10000, 5);
    }

    @Test
    void constructingWithNegativeBudgetThrowsInvalidProjectDataException() {
        assertThrows(InvalidProjectDataException.class,
                () -> new SoftwareProject("X", "Y", -100, 5));
    }

    @Test
    void constructingWithZeroBudgetThrowsInvalidProjectDataException() {
        assertThrows(InvalidProjectDataException.class,
                () -> new SoftwareProject("X", "Y", 0, 5));
    }

    @Test
    void constructingWithZeroTeamSizeThrowsInvalidProjectDataException() {
        assertThrows(InvalidProjectDataException.class,
                () -> new HardwareProject("X", "Y", 5000, 0));
    }

    @Test
    void constructingWithValidDataStoresEveryField() throws InvalidProjectDataException {
        Project p = new HardwareProject("Robot Arm", "Assembly line robot", 20000, 4);
        assertEquals("Robot Arm", p.getName());
        assertEquals("Assembly line robot", p.getDescription());
        assertEquals(20000, p.getBudget());
        assertEquals(4, p.getTeamSize());
    }

    @Test
    void getProjectDetailsIsPolymorphicPerSubclass() throws InvalidProjectDataException {
        Project software = new SoftwareProject("A", "B", 1000, 1);
        Project hardware = new HardwareProject("C", "D", 1000, 1);
        assertEquals(ProjectType.SOFTWARE, software.getProjectDetails());
        assertEquals(ProjectType.HARDWARE, hardware.getProjectDetails());
    }

    @Test
    void completionPercentageWithNoTasksIsZero() {
        assertEquals(0.0, project.getCompletionPercentage());
    }

    @Test
    void completionPercentageWithPartialCompletionIsCalculatedCorrectly() {
        project.addTask(new Task("Task A", TaskStatus.COMPLETED));
        project.addTask(new Task("Task B", TaskStatus.PENDING));
        assertEquals(50.0, project.getCompletionPercentage());
    }

    @Test
    void completionPercentageWithAllTasksCompletedIsHundred() {
        project.addTask(new Task("Task A", TaskStatus.COMPLETED));
        project.addTask(new Task("Task B", TaskStatus.COMPLETED));
        assertEquals(100.0, project.getCompletionPercentage());
    }

    @Test
    void hasTaskNamedFindsExistingTaskCaseInsensitively() {
        project.addTask(new Task("Design Database", TaskStatus.PENDING));
        assertTrue(project.hasTaskNamed("design database"));
        assertFalse(project.hasTaskNamed("Implement API"));
    }
}
