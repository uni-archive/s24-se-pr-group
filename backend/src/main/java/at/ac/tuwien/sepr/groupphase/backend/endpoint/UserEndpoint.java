package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody UserCreateRequest userCreateRequest) throws ValidationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals("ROLE_USER")) && !authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.badRequest().body("User is already logged in");
        }
        if (userCreateRequest.isAdmin()) {
            if (!authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).body("User is not an admin");
            }
        }
        return ResponseEntity.ok(userService.createUser(userMapper.toDto(userCreateRequest)));
    }
}
