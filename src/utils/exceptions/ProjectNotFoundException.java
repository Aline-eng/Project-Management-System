package utils.exceptions;

/**
 * Thrown when a project ID is looked up but no project with that ID exists.
 * Replaces the old "return null and hope the caller checks" pattern with an
 * explicit, impossible-to-ignore signal.
 */
public class ProjectNotFoundException extends Exception {

    // private static final long serialVersionUID = 1L;

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
