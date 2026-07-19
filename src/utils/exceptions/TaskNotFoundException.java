package utils.exceptions;

/**
 * Thrown when a task ID is looked up within a specific project but no task
 * with that ID exists there (whether because the ID is wrong, or because it
 * belongs to a different project entirely).
 */
public class TaskNotFoundException extends Exception {

    // private static final long serialVersionUID = 1L;

    public TaskNotFoundException(String message) {
        super(message);
    }
}
