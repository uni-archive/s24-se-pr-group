package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.MailNotSentException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.UserLockedException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUserDto findApplicationUserByEmail(String email) throws DtoNotFoundException;

    /**
     * Log in a user.
     *
     * @param email    the email
     * @param password the password
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(String email, String password) throws UserLockedException;

    /**
     * Create a new user.
     *
     * @param toCreate the user to create
     * @return the created user
     * @throws ValidationException if the user is invalid
     */
    public ApplicationUserDto createUser(ApplicationUserDto toCreate) throws ValidationException, ForbiddenException,
        MailNotSentException;

    /**
     * Search for users in the persistent data store matching all provided fields.
     *
     * @param searchParameters the search parameters to use in filtering.
     * @return the users where the given fields match.
     */
    Page<ApplicationUserDto> search(ApplicationUserSearchDto searchParameters);


    /**
     * Update the lock status of a user based on the email address.
     *
     * @param toUpdate   the user to update
     * @param adminEmail the email of the admin
     * @return the updated user
     * @throws ValidationException if the email is invalid
     * @throws NotFoundException   if the email does not exist
     */
    ApplicationUserDto updateUserStatusByEmail(ApplicationUserDto toUpdate, String adminEmail) throws ValidationException,
        NotFoundException;

    /**
     * Update the user information.
     *
     * @param userInfo the user information to update
     * @return the updated user information
     * @throws NotFoundException    if the user does not exist
     * @throws ValidationException  if the user information is invalid
     * @throws MailNotSentException if the mail could not be sent
     */
    ApplicationUserDto updateUserInfo(ApplicationUserDto userInfo)
        throws ValidationException, MailNotSentException, DtoNotFoundException;

    /**
     * Find an application user based on the id.
     *
     * @param id the id
     * @return the application user
     * @throws NotFoundException if the user does not exist
     */
    ApplicationUserDto findApplicationUserById(Long id) throws NotFoundException;

    /**
     * Update the email address of a user using a valid token.
     *
     * @param token the token
     * @return the updated user
     */
    ApplicationUserDto updateUserEmailWithValidToken(String token);

    /**
     * Send an email to the user with a link to reset the password.
     *
     * @param email the email
     * @param reset if the password should be reset, otherwise the password should be changed
     * @throws MailNotSentException if the mail could not be sent
     */
    void sendEmailForNewPassword(String email, boolean reset) throws MailNotSentException;

    /**
     * Update the password of a user using a valid token.
     *
     * @param token           the token
     * @param currentPassword the current password
     * @param newPassword     the new password
     * @throws ValidationException  if the new password is invalid
     * @throws DtoNotFoundException if the user does not exist
     */
    void updatePassword(String token, String currentPassword, String newPassword) throws ValidationException,
        DtoNotFoundException;

    /**
     * Activate the account of a user using a valid token.
     *
     * @param token the token
     * @throws ValidationException if the token is invalid
     */
    void activateAccount(String token) throws ValidationException;

    void deleteUser(long id) throws DtoNotFoundException, ValidationException, MailNotSentException;
}
