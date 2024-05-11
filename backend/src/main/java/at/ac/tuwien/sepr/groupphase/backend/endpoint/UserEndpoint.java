package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PermitAll
    @PostMapping(path = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserCreateRequest userCreateRequest) throws ValidationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals("ROLE_USER")) && !authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("User is already logged in");
        }
        if (userCreateRequest.isAdmin()) {
            if (!authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).body("User is not an admin");
            }
        }
        return ResponseEntity.status(201)
            .body(userMapper.toResponse(userService.createUser(userMapper.toDto(userCreateRequest))));
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.toResponse(userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(path = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse findUserByEmail(@RequestParam("email") String email) {
        return userMapper.toResponse(userService.findApplicationUserByEmail(email));
    }

}
