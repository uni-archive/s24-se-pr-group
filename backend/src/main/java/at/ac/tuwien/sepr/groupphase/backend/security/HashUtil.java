package at.ac.tuwien.sepr.groupphase.backend.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String SECRET_KEY = "MIICWwIBAAKBgHX74fcaNnD0ZKe8v8PbhkH6f7AP/lJZXndosE5hXA/ywvyq8scG\n" +
        "ifHm4BROExJS6zBP6SvQw81yW1TTNzFRoxsohxjHUc1aBJmvbE50vzulb1JSPWkq\n" +
        "vXj8ExsQrSOihv5+uLX904EgMZ/HWdl49sb3fODYbZ0KePBkq8ZqWTsxAgMBAAEC\n" +
        "gYBLC7qBiKTUWD+mCgMYO4w8b2/Et4eXLc1OusJaOMdsRYgNW1ctjVU1Dxu9DqRG\n" +
        "5YM/qbSw8At7pJ+/ZtIWhszt3HIO5tiwho/xltkkEQFwdDWWbzM6ajd5CCebTKlX\n" +
        "xpWzL/VAf7XldiC+Yi8GyyB89aWSxoLtX5YHDJ9Fwyft+QJBAMYBBTlgblIEXBkL\n" +
        "6jwOibL43Wx9woaZAbi/dnPqXw/pZtZWZqGH91t7hTSWSBHlYLoWCGOQyONoVtv6\n" +
        "TNrRkwsCQQCYirLTZtrztSuTohJVnjnQ62R4n1nRnLn7lPaxfUjBWlxJhm3wAwpz\n" +
        "Lh+RBgJcq1U6YIR+gYwgQ53UKoCVYNAzAkEAtYqJ96lqfNhyeKRsBtRtfCKhIa/M\n" +
        "gwwWgAVL3cutjTE6kJp3TvMS4FXINAb13TKqFPPOjTtadOZdfSNpA9fTMQJAU00i\n" +
        "3NlFZmMgWe3ez0ypzBJP+qMivJo24lF16nU9XwGGdkxoCvLOKRod+OdsMMkLsOZ2\n" +
        "VFNQf2oNbYiMUYbeCwJADrEftC7FHqXQyM4+Hf/Yb9+8w+YNAvJ9kzzFcFUovLye\n" +
        "Am6pDqtjM1X/Yd0CYQ2qvqOphG/SghVavz8apj6LgQ==";

    public static String generateHMAC(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
