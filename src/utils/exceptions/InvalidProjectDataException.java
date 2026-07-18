package utils.exceptions;

/**
 * Thrown when a project is constructed with data that violates its own
 * invariants (budget or team size not positive). The interactive console
 * flow already prevents this via ValidationUtils before a Project is ever
 * constructed, so in normal use this exists as defense-in-depth: the
 * Project class enforces its own rules regardless of who is calling it,
 * rather than trusting every caller to have validated correctly.
 */
public class InvalidProjectDataException extends Exception {

    // private static final long serialVersionUID = 1L;

    public InvalidProjectDataException(String message) {
        super(message);
    }
}
