package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada upis iz komandne linije ne odgovara zadanom rasponu.
 */
public class InputPredicateException extends RuntimeException {
    public InputPredicateException() {
    }

    public InputPredicateException(String message) {
        super(message);
    }

    public InputPredicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputPredicateException(Throwable cause) {
        super(cause);
    }
}
