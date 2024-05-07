package at.ac.tuwien.sepr.groupphase.backend.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
