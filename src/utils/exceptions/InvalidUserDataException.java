package utils.exceptions;

/**
 * Thrown when a user is created with bad data - a blank name, a badly
 * formatted email, or a blank role.
 */
public class InvalidUserDataException extends Exception {

    // private static final long serialVersionUID = 1L;

    public InvalidUserDataException(String message) {
        super(message);
    }
}
