package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.SecurityUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
                           UserValidator userValidator, UserMapper userMapper) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        ApplicationUserDto applicationUser = findApplicationUserByEmail(email);
        return getUserDetailsForUser(applicationUser);
    }

    private UserDetails getUserDetailsForUser(ApplicationUserDto applicationUser) {
        try {
            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.isAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUserDto findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUserDto applicationUser = userDao.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        try {
            ApplicationUserDto applicationUserDto = findApplicationUserByEmail(userLoginDto.getEmail());
            UserDetails userDetails = getUserDetailsForUser(applicationUserDto);
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
                && passwordEncoder.matches(userLoginDto.getPassword().concat(applicationUserDto.getSalt()), userDetails.getPassword())
            ) {
                List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
                return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
            }
        } catch (NotFoundException e) {
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    public ApplicationUserDto createUser(ApplicationUserDto toCreate) throws ValidationException {
        LOGGER.debug("Create user");
        userValidator.validateForCreate(toCreate);
        toCreate.setSalt(SecurityUtil.generateSalt(32));
        toCreate.setPassword(passwordEncoder.encode(toCreate.getPassword() + toCreate.getSalt()));
        return userDao.create(toCreate);
    }

    @Override
    public Stream<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters) {
        LOGGER.debug("Search for users: {}", searchParameters);
        return userDao.search(searchParameters);
    }

    @Override
    public ApplicationUserDto updateUserStatusByEmail(ApplicationUserDto toUpdate, String adminEmail) throws NotFoundException, ValidationException {
        LOGGER.debug("Update user status: {}", toUpdate);
        userValidator.validateForUpdateStatus(toUpdate, adminEmail);
        ApplicationUserDto user = userDao.findByEmail(toUpdate.getEmail());
        if (user == null) {
            throw new NotFoundException("Could not update the user with the email address " + toUpdate.getEmail() + " because it does not exist");
        }
        try {
            user.setAccountLocked(toUpdate.isAccountLocked());
            return userDao.update(user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }
}
