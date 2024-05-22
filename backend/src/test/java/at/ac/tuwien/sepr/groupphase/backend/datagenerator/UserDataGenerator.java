package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final ApplicationUser ADMIN_USER = new ApplicationUser("admin@email.com", "", "Bert", "Berta", "+430987654321",
        "abcdefghijklmnopqrstuvwxyz", 0, false, UserType.ADMIN, false);
    private static final ApplicationUser CUSTOMER_USER = new ApplicationUser("user@email.com", "", "Erna", "Erna", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.CUSTOMER, false);
    private static final ApplicationUser SUPER_ADMIN_USER = new ApplicationUser("sadmin@email.com", "", "Super", "Admin",
        "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.ADMIN, true);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void generateData() {
        ADMIN_USER.setPassword(passwordEncoder.encode("password" + ADMIN_USER.getSalt()));
        CUSTOMER_USER.setPassword(passwordEncoder.encode("password" + CUSTOMER_USER.getSalt()));
        SUPER_ADMIN_USER.setPassword(passwordEncoder.encode("password" + SUPER_ADMIN_USER.getSalt()));
        userRepository.save(ADMIN_USER);
        userRepository.save(CUSTOMER_USER);
        userRepository.save(SUPER_ADMIN_USER);
    }
}
