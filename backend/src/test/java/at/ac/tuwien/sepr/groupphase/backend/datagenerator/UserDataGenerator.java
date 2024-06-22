package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final ApplicationUser ADMIN_USER = new ApplicationUser("admin@email.com", "", "Bert", "Berta",
        "+4367768822344",
        "abcdefghijklmnopqrstuvwxyz", 0, false, UserType.ADMIN, false, true, new Address());
    private static final ApplicationUser CUSTOMER_USER = new ApplicationUser("user@email.com", "", "Erna", "Erna",
        "+4367768822344", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.CUSTOMER, false, true, new Address());
    private static final ApplicationUser SUPER_ADMIN_USER = new ApplicationUser("sadmin@email.com", "", "Super",
        "Admin",
        "+4367768822344", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.ADMIN, true, true, new Address());
    private static final Logger log = LoggerFactory.getLogger(UserDataGenerator.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    @PostConstruct
    public void generateData() {
        generatePredefinedUsers();
        generateRandomUsers(dataGenerationConfig.userAmount);  // Generate approximately 2000 random users
    }

    private void generatePredefinedUsers() {
        ADMIN_USER.setAddress(new Address("Admin Test street", "1234", "Test city", "Austria"));
        CUSTOMER_USER.setAddress(new Address("Test street", "1234", "Test city", "Austria"));
        SUPER_ADMIN_USER.setAddress(new Address("Test street", "1234", "Test city", "Austria"));
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

    private void generateRandomUsers(Long numberOfUsers) {
        Faker faker = new Faker();
        List<ApplicationUser> users = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        String salt = "zyxwvutsrqponmlkjihgfedcba";  // Using the same salt for all users
        String encodedPassword = passwordEncoder.encode("password" + salt);

        HashMap<String, Boolean> emailMap = new HashMap<>();
        for (int i = 0; i < numberOfUsers; i++) {
            log.info("Generating user " + i + " of " + numberOfUsers);

            String email = null;
            while (email == null || emailMap.containsKey(email)) {
                email = faker.internet().emailAddress();
            }
            emailMap.put(email, true);
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String phoneNumber = faker.phoneNumber().phoneNumber();
            Address address = new Address(faker.address().streetAddress(), faker.address().zipCode(),
                faker.address().city(), faker.address().country());

            addresses.add(address);

            ApplicationUser user = new ApplicationUser(email, "", firstName, lastName, phoneNumber, salt, 0, false,
                UserType.CUSTOMER, false, true, address);
            user.setPassword(encodedPassword);  // Using the same password for all users

            users.add(user);
        }

        addressRepository.saveAll(addresses);
        userRepository.saveAll(users);
    }
}
