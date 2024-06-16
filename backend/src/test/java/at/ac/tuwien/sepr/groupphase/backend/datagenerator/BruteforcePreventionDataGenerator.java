package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeAddress;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeUser;

/**
 * This class is used to provide data to test anything related to login / authentication
 * Reference User-Story: 2.1.2 Authentifizierung
 */
@Profile("generateData")
@Component
public class BruteforcePreventionDataGenerator {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    private void generateData() {
        var addr1 = fakeAddress();
        var customer1 = fakeUser("authentication-212-user01@email.com", passwordEncoder, addr1);

        var addr2 = fakeAddress();
        var customer2 = fakeUser("authentication-212-user02@email.com", passwordEncoder, addr2);

        var addr3 = fakeAddress();
        var admin1 = fakeUser("authentication-212-admin01@email.com", passwordEncoder, addr3);
        admin1.setType(UserType.ADMIN);

        var addr4 = fakeAddress();
        var admin2 = fakeUser("authentication-212-admin02@email.com", passwordEncoder, addr4);
        admin2.setType(UserType.ADMIN);

        addressRepository.saveAll(List.of(addr1, addr2, addr3, addr4));
        userRepository.saveAll(List.of(customer1, customer2, admin1, admin2));
    }
}
