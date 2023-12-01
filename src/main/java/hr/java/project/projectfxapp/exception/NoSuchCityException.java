package hr.java.project.projectfxapp.exception;

/**
 * Baci se u slučaju kada ne postoji navedeni grad.
 */
public class NoSuchCityException extends RuntimeException{
    public NoSuchCityException() {
    }

    public NoSuchCityException(String message) {
        super(message);
    }

    public NoSuchCityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchCityException(Throwable cause) {
        super(cause);
    }
}
