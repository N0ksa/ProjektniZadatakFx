package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada postoji duplicirani matematički projekt.
 */
public class DuplicateMathProjectException extends RuntimeException{
    public DuplicateMathProjectException() {
    }

    public DuplicateMathProjectException(String message) {
        super(message);
    }

    public DuplicateMathProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMathProjectException(Throwable cause) {
        super(cause);
    }
}
