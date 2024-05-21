package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends ApplicationUser {

    @Column(name = "SUPER_ADMIN", updatable = false)
    private boolean superAdmin;

    public Admin(String email, String password, String firstName, String familyName,
                 String phoneNumber, String salt, int loginCount, boolean accountLocked, boolean superAdmin) {
        super(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked);
        this.superAdmin = superAdmin;
    }

    public Admin() {
    }

    @Override
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}
