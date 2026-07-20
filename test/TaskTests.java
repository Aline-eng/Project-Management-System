import enums.TaskStatus;
import models.Task;
import models.user.RegularUser;
import models.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.exceptions.InvalidUserDataException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for Task's status updates and its Completable contract (isCompleted()). */
class TaskTests {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Write Report", TaskStatus.PENDING);
    }

    @Test
    void newTaskStartsWithTheGivenStatus() {
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void pendingTaskIsNotCompleted() {
        assertFalse(task.isCompleted());
    }

    @Test
    void inProgressTaskIsNotCompleted() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertFalse(task.isCompleted());
    }

    @Test
    void settingStatusToCompletedMakesIsCompletedTrue() {
        task.setStatus(TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertTrue(task.isCompleted());
    }

    @Test
    void taskCreatedWithoutAnAssigneeIsUnassigned() {
        assertNull(task.getAssignedTo());
    }

    @Test
    void taskCreatedWithAnAssigneeStoresThatUser() throws InvalidUserDataException {
        User bob = new RegularUser("Bob", "bob@example.com");
        Task assigned = new Task("Design UI", TaskStatus.PENDING, bob);
        assertEquals(bob, assigned.getAssignedTo());
    }

    @Test
    void eachTaskGetsAUniqueId() {
        Task other = new Task("Another Task", TaskStatus.PENDING);
        assertNotEquals(task.getId(), other.getId());
    }
}
