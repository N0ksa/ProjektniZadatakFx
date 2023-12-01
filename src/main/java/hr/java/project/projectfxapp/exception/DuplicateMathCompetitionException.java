package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada postoji duplicirano matematičko natjecanje.
 */
public class DuplicateMathCompetitionException extends RuntimeException {
    public DuplicateMathCompetitionException() {
    }

    public DuplicateMathCompetitionException(String message) {
        super(message);
    }

    public DuplicateMathCompetitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMathCompetitionException(Throwable cause) {
        super(cause);
    }
}
