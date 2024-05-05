package at.ac.tuwien.sepr.groupphase.backend.persistence.exception;

public class EntityNotFoundException extends Exception {

        public EntityNotFoundException(Long id) {
            super("Entity with id " + id + " not found.");
        }
}
