package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;

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
            .setLoginCount(0);
        return applicationUserDto;
    }

}
