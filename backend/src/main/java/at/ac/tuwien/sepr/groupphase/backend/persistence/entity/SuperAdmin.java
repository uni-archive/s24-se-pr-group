package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends ApplicationUser {

    public SuperAdmin(String email, String password, String firstName, String familyName,
                      String phoneNumber, String salt, int loginCount, boolean accountLocked) {
        super(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked);
    }

    public SuperAdmin() {
    }

    @Override
    public boolean isSuperAdmin() {
        return true;
    }

}
