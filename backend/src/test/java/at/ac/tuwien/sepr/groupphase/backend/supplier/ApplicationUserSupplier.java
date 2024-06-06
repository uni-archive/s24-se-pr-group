package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ApplicationUserSupplier {

    public static ApplicationUserDto anAdminUser() {
        ApplicationUserDto applicationUserDto = new ApplicationUserDto()
            .setEmail("admin@email.com")
            .setFirstName("Berta")
            .setFamilyName("Muster")
            .setPhoneNumber("+436641234567")
            .setPassword("password")
            .setAdmin(true)
            .setAccountLocked(false)
            .setLoginCount(0)
            .setAddress(AddressSupplier.anAddress());
        return applicationUserDto;
    }

    public static ApplicationUserDto aCustomerUser() {
        return new ApplicationUserDto()
            .setEmail("user@email.com")
            .setFirstName("John")
            .setFamilyName("Doe")
            .setPhoneNumber("+436642345678")
            .setPassword("password")
            .setAdmin(false)
            .setAccountLocked(false)
            .setLoginCount(0);
    }

    public static ApplicationUser anAdminUserEntity() {
        return new ApplicationUser()
            .setEmail("admin2@email.com")
            .setFirstName("Berta")
            .setFamilyName("Muster")
            .setPhoneNumber("+436641234567")
            .setPassword("password")
            .setType(UserType.ADMIN)
            .setAccountLocked(false)
            .setLoginCount(0);
    }

    public static ApplicationUser aUserEntity() {
        return new ApplicationUser()
            .setEmail("user@email.com")
            .setFirstName("Berta")
            .setFamilyName("Muster")
            .setPhoneNumber("+436641234567")
            .setPassword("password")
            .setType(UserType.CUSTOMER)
            .setAccountLocked(false)
            .setLoginCount(0);
    }

    public static ApplicationUser aUserEntity(String mail, PasswordEncoder passwordEncoder, Faker faker) {
        var usr = faker.name();
        return new ApplicationUser()
            .setEmail(mail)
            .setFirstName(usr.firstName())
            .setFamilyName(usr.lastName())
            .setPhoneNumber(faker.phoneNumber().phoneNumber())
            .setSalt("salt")
            .setPassword(passwordEncoder.encode("password" + "salt"))
            .setType(UserType.CUSTOMER)
            .setAccountLocked(false)
            .setLoginCount(0);
    }
}
