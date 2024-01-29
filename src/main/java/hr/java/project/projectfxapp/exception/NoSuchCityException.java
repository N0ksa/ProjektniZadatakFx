package hr.java.project.projectfxapp.exception;

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
