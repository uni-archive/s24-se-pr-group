package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends ApplicationUser {
    public Customer(String email, String password, String firstName, String familyName,
                    String phoneNumber, String salt, int loginCount, boolean accountLocked) {
        super(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked);
    }

    public Customer() {
    }
}
