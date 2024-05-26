package at.ac.tuwien.sepr.groupphase.backend.service.exception;

public class MailNotSentException extends Exception {

    public MailNotSentException(String message) {
        super(message);
    }

    public MailNotSentException(String message, Throwable cause) {
        super(message, cause);
    }
}
