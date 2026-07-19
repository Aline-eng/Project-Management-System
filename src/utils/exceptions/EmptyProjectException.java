package utils.exceptions;

/**
 * Thrown when an operation that requires at least one task is attempted on
 * a project that has none - specifically, checking a single project's
 * completion percentage on demand. This is deliberately NOT thrown by the
 * aggregate Status Report table, which still shows empty projects at 0.00%
 * alongside every other project - that's a valid, expected state for an
 * overview table, not an error. Asking "how complete is project X"
 * about a project with nothing defined yet is a more meaningful place to
 * flag emptiness as exceptional.
 */
public class EmptyProjectException extends Exception {

    // private static final long serialVersionUID = 1L;

    public EmptyProjectException(String message) {
        super(message);
    }
}
