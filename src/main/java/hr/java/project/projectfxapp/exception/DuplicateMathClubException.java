package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada postoji duplicirani matematički klub.
 */
public class DuplicateMathClubException extends RuntimeException {
    public DuplicateMathClubException() {
    }

    public DuplicateMathClubException(String message) {
        super(message);
    }

    public DuplicateMathClubException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMathClubException(Throwable cause) {
        super(cause);
    }
}
