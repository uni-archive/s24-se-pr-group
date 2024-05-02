package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;

@Entity
public class Admin extends ApplicationUser{
    public Admin(String email, String password, String firstName, String familyName,
                    String phoneNumber, String salt, int loginCount, boolean accountLocked) {
        super(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked);
    }

    public Admin() {
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}
