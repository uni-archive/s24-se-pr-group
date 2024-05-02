package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation
@Repository
public class UserRepository {

    private final Customer user;
    private final Admin admin;

    @Autowired
    public UserRepository(PasswordEncoder passwordEncoder) {
        user = new Customer("user@email.com", passwordEncoder.encode("password"),
            "Bert", "Berta", "+431234567890", "sus", 0, false);
        admin = new Admin("admin@email.com", passwordEncoder.encode("password"),
            "Ernie", "Erna", "+430987654321", "sussy", 0, false);
    }

    public ApplicationUser findUserByEmail(String email) {
        if (email.equals(user.getEmail())) {
            return user;
        }
        if (email.equals(admin.getEmail())) {
            return admin;
        }
        return null; // In this case null is returned to fake Repository behavior
    }


}
