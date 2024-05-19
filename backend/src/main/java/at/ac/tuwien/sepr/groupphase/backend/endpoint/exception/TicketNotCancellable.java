package at.ac.tuwien.sepr.groupphase.backend.endpoint.exception;

public class TicketNotCancellable extends Exception {
    public TicketNotCancellable(Throwable cause) {
        super(cause);
    }

    public TicketNotCancellable(String message, Throwable cause) {
        super(message, cause);
    }
}
