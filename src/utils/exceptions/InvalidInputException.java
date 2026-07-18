package utils.exceptions;

/**
 * Thrown when user-supplied input fails a validation rule that isn't
 * already handled by ValidationUtils' interactive retry loops - e.g. a
 * budget range where the minimum exceeds the maximum. A checked exception,
 * so every call site that can trigger it must explicitly handle it.
 */
public class InvalidInputException extends Exception {

    // private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
