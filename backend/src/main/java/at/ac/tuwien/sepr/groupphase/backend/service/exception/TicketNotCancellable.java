package at.ac.tuwien.sepr.groupphase.backend.service.exception;

public class TicketNotCancellable extends ValidationException {
    public TicketNotCancellable(String message) {
        super(message);
    }
}
