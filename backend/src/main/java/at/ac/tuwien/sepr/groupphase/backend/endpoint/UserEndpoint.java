package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String RESPONSE_KEY = "message";
    private final UserService userService;
    private final ApplicationUserMapper userMapper;

    public UserEndpoint(UserService userService, ApplicationUserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PermitAll
    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserCreateRequest userCreateRequest)
        throws ForbiddenException, MailNotSentException {
        LOGGER.info("Register user: {}", userCreateRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals(Code.USER)) && !authentication.getAuthorities().stream()
            .anyMatch(r -> r.getAuthority().equals(Code.ADMIN))) {
            return ResponseEntity.status(403).body("User is already logged in");
        }
        if (userCreateRequest.isAdmin()) {
            if (!authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(Code.ADMIN))) {
                return ResponseEntity.status(403).body("User is not an admin");
            }
        }
        ApplicationUserDto dto = userMapper.toDto(userCreateRequest);

        try {
            return ResponseEntity.status(201)
                .body(userMapper.toResponse(userService.createUser(dto)));
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Secured(Code.USER)
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse getUser() {
        LOGGER.info("Get current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return userMapper.toResponse(userService.findApplicationUserByEmail(authentication.getName()));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    @Secured(Code.ADMIN)
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ApplicationUserResponse> searchUsers(
        @RequestParam(name = "firstName", defaultValue = "", required = false) String firstName,
        @RequestParam(name = "familyName", defaultValue = "", required = false) String familyName,
        @RequestParam(name = "email", defaultValue = "", required = false) String email,
        @RequestParam(name = "isLocked", defaultValue = "false", required = false) Boolean isLocked,
        @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "15", required = false) Integer size) {
        LOGGER.info("Search users by first name: {}, family name: {}, email: {}, is locked: {}", firstName, familyName,
            email, isLocked);
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto(firstName, familyName, email, isLocked,
            PageRequest.of(page, size));
        return userService.search(searchParams).map(userMapper::toResponse);
    }

    @Secured(Code.ADMIN)
    @PutMapping(path = "/update/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse updateUserStatusByEmail(@RequestBody ApplicationUserDto user) {
        LOGGER.info("Update user status by email: {}", user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return userMapper.toResponse(userService.updateUserStatusByEmail(user, authentication.getName()));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Secured("ROLE_USER")
    @PutMapping(path = "/update/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse updateUserInfo(@RequestBody UserUpdateInfoRequest userInfo) throws MailNotSentException {
        LOGGER.info("Update user info: {}", userInfo);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ApplicationUserDto user;
        try {
            user = userService.findApplicationUserById(userInfo.id());
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        if (!authentication.getName().equals(user.getEmail())) {
            throw new ValidationException("Du kannst nur deine eigenen Daten bearbeiten.");
        }
        try {
            return userMapper.toResponse(userService.updateUserInfo(userMapper.toDto(userInfo)));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @PermitAll
    @GetMapping("/update/user/email")
    public ResponseEntity<?> updateUserEmailWithValidToken(@RequestParam("token") String token) {
        LOGGER.info("Update user email with token: {}", token);
        if (userService.updateUserEmailWithValidToken(token) != null) {
            return ResponseEntity.ok("Deine E-Mail-Adresse wurde erfolgreich geändert. Bitte melde dich mit deinen "
                + "neuen Zugangsdaten an.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dieser Link ist nicht gültig.");
    }

    @PermitAll
    @GetMapping(path = "/user/password/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendEmailForPasswordReset(@RequestParam("email") String email) throws MailNotSentException {
        LOGGER.info("Send email for password reset to: {}", email);
        Map<String, String> response = new HashMap<>();
        userService.sendEmailForNewPassword(email, true);
        response.put(RESPONSE_KEY, "E-Mail zum Zurücksetzen des Passworts wurde gesendet");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PermitAll
    @GetMapping(path = "/user/password/change", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendEmailForPasswordChange(@RequestParam("email") String email) throws MailNotSentException {
        LOGGER.info("Send email for password change to: {}", email);
        Map<String, String> response = new HashMap<>();
        userService.sendEmailForNewPassword(email, false);
        response.put(RESPONSE_KEY, "E-Mail zum Ändern des Passworts wurde gesendet.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PermitAll
    @PutMapping("/user/password/update")
    public ResponseEntity<Map<String, String>> setNewPasswordWithValidToken(@RequestParam("token") String token,
                                                                            @RequestParam(required = false, name =
                                                                                "currentPassword") String currentPassword,
                                                                            @RequestParam("newPassword") String newPassword) {
        LOGGER.info("Set new password with token: {}", token);
        Map<String, String> response = new HashMap<>();
        try {
            userService.updatePassword(token, currentPassword, newPassword);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        response.put(RESPONSE_KEY, "Dein Passwort wurde erfolgreich geändert.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PermitAll
    @PostMapping("/user/activate/account")
    public ResponseEntity<Map<String, String>> activateAccount(@RequestParam("token") String token) {
        LOGGER.info("Activate account with token: {}", token);
        Map<String, String> response = new HashMap<>();
        try {
            userService.activateAccount(token);
        } catch (at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
        response.put(RESPONSE_KEY, "Dein Konto wurde erfolgreich aktiviert. Du kannst dich nun anmelden.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PermitAll
    @DeleteMapping("/user/delete")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam("id") long id) throws MailNotSentException {
        LOGGER.info("Delete user with id: {}", id);
        Map<String, String> response = new HashMap<>();
        try {
            userService.deleteUser(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        response.put(RESPONSE_KEY, "Dein Account wurde erfolgreich gelöscht.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
