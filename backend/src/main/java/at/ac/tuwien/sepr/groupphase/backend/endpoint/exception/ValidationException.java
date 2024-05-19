package at.ac.tuwien.sepr.groupphase.backend.endpoint.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
