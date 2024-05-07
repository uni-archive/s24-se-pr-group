package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class UserDataGenerator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void generateData() {
        ADMIN_USER.setPassword(passwordEncoder.encode("password"+ADMIN_USER.getSalt()));
        CUSTOMER_USER.setPassword(passwordEncoder.encode("password"+ADMIN_USER.getSalt()));
        userRepository.save(ADMIN_USER);
        userRepository.save(CUSTOMER_USER);
    }

    private static final Admin ADMIN_USER = new Admin("admin@email.com", "", "Bert", "Berta", "+430987654321", "abcdefghijklmnopqrstuvwxyz", 0, false);
    private static final Admin CUSTOMER_USER = new Admin("user@email.com", "", "Erna", "Erna", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false);
}
