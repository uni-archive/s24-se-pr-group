package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;

@Entity
public class Customer extends ApplicationUser {
    public Customer(String email, String password, String firstName, String familyName,
                           String phoneNumber, String salt, int loginCount, boolean accountLocked) {
        super(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked);
    }

    public Customer() {
    }
}
