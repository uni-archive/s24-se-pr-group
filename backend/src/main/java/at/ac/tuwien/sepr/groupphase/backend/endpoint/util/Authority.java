package at.ac.tuwien.sepr.groupphase.backend.endpoint.util;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ADMIN(Code.ADMIN),
    USER(Code.USER),
    SUPER_ADMIN(Code.SUPER_ADMIN);

    private final String authority;

    Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public class Code {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
        public static final String SUPER_ADMIN = "ROLE_SUPERADMIN";
    }
}
