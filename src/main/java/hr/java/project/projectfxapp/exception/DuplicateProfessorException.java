package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada postoji duplicirani profesor.
 */
public class DuplicateProfessorException extends RuntimeException {
    public DuplicateProfessorException() {
    }

    public DuplicateProfessorException(String message) {
        super(message);
    }

    public DuplicateProfessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateProfessorException(Throwable cause) {
        super(cause);
    }
}
