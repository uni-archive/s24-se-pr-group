package at.ac.tuwien.sepr.groupphase.backend.service.exception;

public class DtoNotFoundException extends Exception {
    public DtoNotFoundException(Throwable cause) {
        super(cause);
    }

    public DtoNotFoundException(String message) {
        super(message);
    }
}
