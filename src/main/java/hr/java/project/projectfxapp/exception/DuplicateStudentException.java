package hr.java.project.projectfxapp.exception;

/**
 * Baci se kada postoji duplicirani student.
 */
public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException() {
    }

    public DuplicateStudentException(String message) {
        super(message);
    }

    public DuplicateStudentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateStudentException(Throwable cause) {
        super(cause);
    }
}
