package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
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
        "abcdefghijklmnopqrstuvwxyz", 0, false, UserType.ADMIN, false, new Address());
    private static final ApplicationUser CUSTOMER_USER = new ApplicationUser("user@email.com", "", "Erna", "Erna", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.CUSTOMER, false,  new Address());
    private static final ApplicationUser SUPER_ADMIN_USER = new ApplicationUser("sadmin@email.com", "", "Super", "Admin",
        "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.ADMIN, true,  new Address());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    private void generateData() {
        ADMIN_USER.setAddress(new Address("Admin Test street", "1234", "Test city", "Test country"));
        CUSTOMER_USER.setAddress(new Address("Test street", "1234", "Test city", "Test country"));
        SUPER_ADMIN_USER.setAddress(new Address("Test street", "1234", "Test city", "Test country"));
        addressRepository.save(ADMIN_USER.getAddress());
        addressRepository.save(CUSTOMER_USER.getAddress());
        addressRepository.save(SUPER_ADMIN_USER.getAddress());
        ADMIN_USER.setPassword(passwordEncoder.encode("password" + ADMIN_USER.getSalt()));
        CUSTOMER_USER.setPassword(passwordEncoder.encode("password" + CUSTOMER_USER.getSalt()));
        SUPER_ADMIN_USER.setPassword(passwordEncoder.encode("password" + SUPER_ADMIN_USER.getSalt()));
        userRepository.save(ADMIN_USER);
        userRepository.save(CUSTOMER_USER);
        userRepository.save(SUPER_ADMIN_USER);
    }
}
