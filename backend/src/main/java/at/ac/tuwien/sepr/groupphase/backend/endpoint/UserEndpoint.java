package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AddressResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;
    private final AddressResponseMapper addressMapper;
    private final AddressService addressService;

    public UserEndpoint(UserService userService, UserMapper userMapper, AddressResponseMapper addressMapper,
        AddressService addressService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
        this.addressService = addressService;
    }

    @PermitAll
    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserCreateRequest userCreateRequest)
        throws ValidationException, ForbiddenException {
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

        return ResponseEntity.status(201)
            .body(userMapper.toResponse(userService.createUser(dto)));
    }

    @Secured(Code.USER)
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.toResponse(userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured(Code.ADMIN)
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ApplicationUserDto> searchUsers(
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "familyName", required = false) String familyName,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "isLocked", required = false) Boolean isLocked) {
        LOGGER.info("Search users by first name: {}, family name: {}, email: {}, is locked: {}", firstName, familyName,
            email, isLocked);
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto(firstName, familyName, email, isLocked);
        return userService.search(searchParams).toList();
    }

    @Secured(Code.ADMIN)
    @PutMapping(path = "/update/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse updateUserStatusByEmail(@RequestBody ApplicationUserDto user) throws ValidationException,
        NotFoundException {
        LOGGER.info("Update user status by email: {}", user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.toResponse(userService.updateUserStatusByEmail(user, authentication.getName()));
    }

    @Secured("ROLE_USER")
    @PutMapping(path = "/update/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationUserResponse updateUserInfo(@RequestBody UserUpdateInfoRequest userInfo) throws ValidationException, MailNotSentException {
        LOGGER.info("Update user info: {}", userInfo);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ApplicationUserDto user = userService.findApplicationUserById(userInfo.id());
        if (!authentication.getName().equals(user.getEmail())) {
            throw new ValidationException("User can only update their own information.");
        }
        return userMapper.toResponse(userService.updateUserInfo(userMapper.toDto(userInfo)));
    }

}
