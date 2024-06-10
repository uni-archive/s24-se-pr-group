package at.ac.tuwien.sepr.groupphase.backend.service.exception;

public class UserLockedException extends Exception {

    public UserLockedException(String message) {
        super(message);
    }
}
