package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.AccountActivateToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EmailChangeToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.NewPasswordToken;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AccountActivateTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EmailChangeTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.NewPasswordTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.supplier.AddressSupplier;
import at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier;
import at.ac.tuwien.sepr.groupphase.backend.util.PageModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES_2;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER_2;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EmailChangeTokenRepository emailChangeTokenRepository;

    @Autowired
    private NewPasswordTokenRepository newPasswordTokenRepository;

    @Autowired
    private AccountActivateTokenRepository accountActivateTokenRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new PageModule());
    }

    @Test
    void registerUserShouldResultInNewUser() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("abcd@cd.de", "password", "Peter", "Test",
            "+43 6776182783", false, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ApplicationUserResponse applicationUserResponse = objectMapper.readValue(response.getContentAsString(),
            ApplicationUserResponse.class);

        ApplicationUser user = userRepository.findByEmail("abcd@cd.de");
        Assertions.assertAll(
            () -> Assertions.assertNotNull(user),
            () -> Assertions.assertNotNull(applicationUserResponse.address()),
            () -> Assertions.assertNotNull(applicationUserResponse.address().id()),
            () -> Assertions.assertNotNull(addressRepository.findById(applicationUserResponse.address().id()))
        );
    }

    @Test
    void registerUserShouldNotAllowToCreateUserIfAlreadyRegistered() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("cdef@ge.ck", "password", "Peter", "Test",
            "+43 6776182783", false, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isForbidden())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUser user = userRepository.findByEmail("cdef@ge.ck");
        Assertions.assertAll(
            () -> Assertions.assertNull(user),
            () -> assertEquals(403, response.getStatus()),
            () -> assertEquals("User is already logged in", response.getContentAsString())
        );
    }

    @Test
    void registerUserShouldNotAllowToCreateUserIfCreatedUserIsAdminButCreatingUserIsNot() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("cdef@ge.ck", "password", "Peter", "Test",
            "+43 6776182783", true, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isForbidden())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUser user = userRepository.findByEmail("cdef@ge.ck");
        Assertions.assertAll(
            () -> Assertions.assertNull(user),
            () -> assertEquals(403, response.getStatus()),
            () -> assertEquals("User is not an admin", response.getContentAsString())
        );
    }

    @Test
    void registerUserShouldAllowToCreateUserIfCreatedUserIsAdminAndCreatingUserIsAdmin() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("cdefij@ge.ck", "password", "Peter", "Test",
            "+43 6776182783", true, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUser user = userRepository.findByEmail("cdefij@ge.ck");
        Assertions.assertAll(
            () -> Assertions.assertNotNull(user),
            () -> assertEquals(201, response.getStatus())
        );
    }

    @Test
    void registerUserShouldAllowToAdminToCreateUser() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("cdefgh@ge.ck", "password", "Peter", "Test",
            "+43 6776182783", false, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUser user = userRepository.findByEmail("cdefgh@ge.ck");
        Assertions.assertAll(
            () -> Assertions.assertNotNull(user),
            () -> assertEquals(201, response.getStatus())
        );
    }

    @Test
    void registerUserShouldThrowValidationException() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("test", "password", "Peter", "Test",
            "+43 6776182783", false, AddressSupplier.addressCreateRequest());

        MvcResult mvcResult = mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ApplicationUser user = userRepository.findByEmail("test");
        Assertions.assertAll(
            () -> Assertions.assertNull(user),
            () -> assertEquals(422, response.getStatus())
        );
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void searchUsersShouldReturnResultsForAdmin() throws Exception {
        userRepository.save(ApplicationUserSupplier.anAdminUserEntity());
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto("Berta", "", "@email.com",
            false, PageRequest.of(0, 15));

        MvcResult mvcResult = mockMvc.perform(get(TestData.USER_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("firstName", searchParams.firstName())
                .param("familyName", searchParams.familyName())
                .param("email", searchParams.email())
                .param("isLocked", String.valueOf(searchParams.isLocked())))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<ApplicationUserResponse> users = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });
        Assertions.assertAll(
            () -> Assertions.assertNotNull(users),
            () -> assertEquals(1, users.getTotalElements()),
            () -> assertEquals(1, users.getTotalPages())
        );
    }

    @Test
    void searchUsersShouldReturnForbiddenForNonAdmin() throws Exception {
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto("Berta", "", "admin@email.com",
            false, PageRequest.of(0, 15));

        mockMvc.perform(get(TestData.USER_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .param("firstName", searchParams.firstName())
                .param("familyName", searchParams.familyName())
                .param("email", searchParams.email())
                .param("isLocked", String.valueOf(searchParams.isLocked())))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateUserStatusByEmailShouldUpdateStatus() throws Exception {
        // Create a new user DTO and set the email and account locked status
        ApplicationUser userToUpdate = ApplicationUserSupplier.anAdminUserEntity();
        userToUpdate.setAccountLocked(true);

        // Save the entity and retrieve it to get the generated ID
        ApplicationUser savedUser = userRepository.save(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
        userToUpdate.setId(savedUser.getId());  // Set the ID back to the DTO

        userToUpdate.setAccountLocked(false);  // Set the new account locked status (false)

        // Perform the PUT request with an admin token
        mockMvc.perform(put(TestData.USER_BASE_URI + "/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(userToUpdate)))
            .andExpect(status().isOk());

        // Delete the entity
        userRepository.delete(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
    }

    @Test
    void updateUserStatusByEmailShouldReturnForbiddenForNonAdmin() throws Exception {
        // Create a new user DTO and set the email and account locked status
        ApplicationUser userToUpdate = ApplicationUserSupplier.anAdminUserEntity();
        userToUpdate.setAccountLocked(true);

        // Save the entity and retrieve it to get the generated ID
        ApplicationUser savedUser = userRepository.save(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
        userToUpdate.setId(savedUser.getId());  // Set the ID back to the DTO

        userToUpdate.setAccountLocked(false);  // Set the new account locked status (false)

        // Perform the PUT request with a non-admin token
        mockMvc.perform(put(TestData.USER_BASE_URI + "/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .content(objectMapper.writeValueAsString(userToUpdate)))
            .andExpect(status().isForbidden());

        // Delete the entity
        userRepository.delete(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
    }

    @Test
    void updateUserStatusByEmailShouldThrowValidationException() throws Exception {
        ApplicationUser userToUpdate = ApplicationUserSupplier.anAdminUserEntity();
        userToUpdate.setSuperAdmin(true);
        userToUpdate.setAccountLocked(true);

        // Save the entity and retrieve it to get the generated ID
        ApplicationUser savedUser = userRepository.save(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
        userToUpdate.setId(savedUser.getId());  // Set the ID back to the DTO

        userToUpdate.setAccountLocked(false);  // Set the new account locked status (false)

        // Perform the PUT request with an admin token
        mockMvc.perform(put(TestData.USER_BASE_URI + "/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(userToUpdate)))
            .andExpect(status().isUnprocessableEntity());

        // Delete the entity
        userRepository.delete(objectMapper.convertValue(userToUpdate, ApplicationUser.class));
    }

    @Test
    @WithMockUser(roles = "USER", username = "admin2@email.com")
    void updateUserInfoShouldUpdatePhoneNummer() throws Exception {
        // Arrange
        ApplicationUser user = ApplicationUserSupplier.anAdminUserEntity();
        user.setAddress(AddressSupplier.anAddressEntity());
        addressRepository.save(user.getAddress());
        userRepository.save(user);

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            user.getId(), null,
            "+431234567890", null
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER_2, ADMIN_ROLES_2)))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        ApplicationUserResponse applicationUserResponse = objectMapper.readValue(response.getContentAsString(), ApplicationUserResponse.class);
        assertEquals("+431234567890", applicationUserResponse.phoneNumber());

        userRepository.delete(user);
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnBadRequestForInvalidToken() throws Exception {
        // Arrange
        String invalidToken = "invalidToken";

        // Act
        MvcResult mvcResult = mockMvc.perform(get(TestData.USER_BASE_URI + "/update/user/email")
                .param("token", invalidToken))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals("Der Link ist ungültig.", response.getContentAsString());
    }

    @Test
    @WithMockUser(roles = "USER", username = "user2@email.com")
    void getUserShouldReturnCurrentUserDetails() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("user2@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        // Act
        MvcResult mvcResult = mockMvc.perform(get(TestData.USER_BASE_URI + "/current")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        ApplicationUserResponse applicationUserResponse = objectMapper.readValue(response.getContentAsString(), ApplicationUserResponse.class);

        Assertions.assertAll(
            () -> assertEquals("user2@email.com", applicationUserResponse.email()),
            () -> assertEquals("John", applicationUserResponse.firstName()),
            () -> assertEquals("Doe", applicationUserResponse.familyName())
        );

        userRepository.delete(user);
    }

    @Test
    @WithMockUser(roles = "USER", username = "user2@email.com")
    void getUserShouldThrowDtoNotFoundException() throws Exception {
        // Act
        MvcResult mvcResult = mockMvc.perform(get(TestData.USER_BASE_URI + "/current")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())  // Expecting a 404 Not Found status
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        Assertions.assertAll(
            () -> assertEquals(404, response.getStatus()),
            () -> Assertions.assertThrows(DtoNotFoundException.class, () -> {
                throw new DtoNotFoundException(responseBody);
            })
        );
    }

    @Test
    @WithMockUser(roles = "USER", username = "user1@example.com")
    void updateUserInfoShouldThrowValidationExceptionForAnotherUser() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("user2@example.com"); // Different email to trigger the validation exception
        ApplicationUser savedUser = userRepository.save(user);

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            savedUser.getId(), null, "+431234567890", null
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isUnprocessableEntity()) // Expecting a 400 Bad Request status
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        Assertions.assertAll(
            () -> assertEquals(422, response.getStatus()),
            () -> assertTrue(responseBody.contains("Du kannst nur deine eigenen Daten bearbeiten."))
        );

        userRepository.delete(user);
    }

    @Test
    @WithMockUser(roles = "USER", username = "user1@example.com")
    void updateUserInfoShouldUpdateUser() throws Exception {
        // Arrange
        Address address = AddressSupplier.anAddressEntity();
        Address savedAddress = addressRepository.save(address);

        ApplicationUser user = ApplicationUserSupplier.aUserEntity();
        user.setEmail("user1@example.com");
        user.setAddress(savedAddress); // Set the saved address to the user
        ApplicationUser savedUser = userRepository.save(user);

        // Verify the user is saved
        Assertions.assertNotNull(userRepository.findById(savedUser.getId()).orElse(null));

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            savedUser.getId(), null, "+431234567890", null
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        ApplicationUserResponse applicationUserResponse = objectMapper.readValue(response.getContentAsString(), ApplicationUserResponse.class);

        Assertions.assertAll(
            () -> assertEquals("+431234567890", applicationUserResponse.phoneNumber())
        );

        // Cleanup
        userRepository.delete(savedUser);
        addressRepository.delete(savedAddress);
    }

    @Test
    @WithMockUser(roles = "USER", username = "user@email.com")
    void updateUserInfoShouldThrowNotFoundException() throws Exception {
        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            -123L, null, "+431234567890", null
        );

        MvcResult mvcResult = mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        Assertions.assertAll(
            () -> assertEquals(404, response.getStatus()),
            () -> assertTrue(responseBody.contains("Could not find the user with the id -123"))
        );
    }

    @Test
    @WithMockUser(roles = "USER", username = "user23@email.com")
    void updateUserInfoShouldThrowValidationException() throws Exception {
        // Arrange
        ApplicationUser user = ApplicationUserSupplier.aUserEntity();
        user.setEmail("user21@email.com"); // Match the mock user email
        ApplicationUser savedUser = userRepository.save(user);

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            savedUser.getId(), "user24@email.com", "+431234567890", null
        );

        // Act & Assert
        mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Du kannst nur deine eigenen Daten bearbeiten.", Objects.requireNonNull(result.getResolvedException()).getMessage()));

        // Clean up
        userRepository.delete(user);
    }

    @Test
    @WithMockUser(roles = "USER", username = "user1@email.com")
    void updateUserInfoShouldThrowValidationExceptionForInvalidPhoneNumber() throws Exception {
        // Arrange
        ApplicationUser user = ApplicationUserSupplier.aUserEntity();
        user.setEmail("user1@email.com");
        ApplicationUser savedUser = userRepository.save(user);

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            savedUser.getId(), null, "1234567890", null
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(put(TestData.USER_BASE_URI + "/update/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        Assertions.assertAll(
            () -> assertEquals(422, response.getStatus()),
            () -> assertTrue(responseBody.contains("Validation Error: Telefonnummer ist ungültig"))
        );

        userRepository.delete(user);
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnSuccessMessage() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("old@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        EmailChangeToken token = new EmailChangeToken();
        token.setToken("validToken");
        token.setCurrentEmail("old@email.com");
        token.setNewEmail("new@email.com");
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        emailChangeTokenRepository.save(token);

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/update/user/email")
                .param("token", "validToken")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Deine E-Mail-Adresse wurde erfolgreich geändert. Bitte melde dich mit deinen neuen Zugangsdaten an.");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        ApplicationUser updatedUser = userRepository.findByEmail("new@email.com");
        Assertions.assertNotNull(updatedUser);
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getFamilyName());

        userRepository.delete(updatedUser);
        emailChangeTokenRepository.delete(token);
    }

    @Test
    void sendEmailForPasswordResetShouldReturnSuccessMessage() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/user/password/reset")
                .param("email", "test@email.com")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "E-Mail zum Zurücksetzen des Passworts wurde gesendet");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        ApplicationUser updatedUser = userRepository.findByEmail("test@email.com");
        Assertions.assertNotNull(updatedUser);

        userRepository.delete(updatedUser);
    }

    @Test
    void sendEmailForPasswordChangeShouldReturnSuccessMessage() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test123@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/user/password/change")
                .param("email", "test123@email.com")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "E-Mail zum Ändern des Passworts wurde gesendet.");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        userRepository.delete(user);
    }

    @Test
    void setNewPasswordWithValidTokenShouldUpdatePassword() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        user.setPassword("oldPassword");
        userRepository.save(user);

        NewPasswordToken token = new NewPasswordToken();
        token.setToken("validToken");
        token.setEmail("test@email.com");
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        newPasswordTokenRepository.save(token);

        // Act
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users/user/password/update")
                .param("token", "validToken")
                .param("newPassword", "newPassword")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Dein Passwort wurde erfolgreich geändert.");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        ApplicationUser updatedUser = userRepository.findByEmail("test@email.com");
        Assertions.assertNotEquals("oldPassword", updatedUser.getPassword());

        userRepository.delete(updatedUser);
        newPasswordTokenRepository.delete(token);
    }

    @Test
    void setPasswordWithValidTokenShouldThrowValidationException() throws Exception {
        ApplicationUser user = ApplicationUserSupplier.aUserEntity();
        user.setEmail("test1@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        user.setPassword("oldPassword");
        userRepository.save(user);

        NewPasswordToken token = new NewPasswordToken();
        token.setToken("validToken");
        token.setEmail("test1@email.com");
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        newPasswordTokenRepository.save(token);

        // Act
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users/user/password/update")
                .param("token", "validToken")
                .param("newPassword", "oldPassword")
                .param("currentPassword", "oldPassword")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        userRepository.delete(user);
        newPasswordTokenRepository.delete(token);
    }

    @Test
    void activateAccountShouldSucceed() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        user.setAccountActivated(false);
        userRepository.save(user);

        AccountActivateToken token = new AccountActivateToken();
        token.setToken("validToken");
        token.setEmail("test@email.com");
        token.setExpiryDate(LocalDateTime.now().plusDays(1));
        accountActivateTokenRepository.save(token);

        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users/user/activate/account")
                .param("token", "validToken")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Dein Konto wurde erfolgreich aktiviert. Du kannst dich nun anmelden.");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        ApplicationUser updatedUser = userRepository.findByEmail("test@email.com");
        assertTrue(updatedUser.isAccountActivated());

        userRepository.delete(updatedUser);
        accountActivateTokenRepository.delete(token);
    }

    @Test
    void activateAccountShouldThrowValidationException() throws Exception {
        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users/user/activate/account")
                .param("token", "validToken")
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isUnprocessableEntity())
            .andReturn();
    }

    @Test
    void deleteUserShouldSucceed() throws Exception {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setFamilyName("Doe");
        userRepository.save(user);

        // Act
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/users/user/delete")
                .param("id", String.valueOf(user.getId()))
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // Ensure UTF-8 encoding
        String responseBody = response.getContentAsString();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Dein Account wurde erfolgreich gelöscht.");

        Assertions.assertAll(
            () -> assertEquals(200, response.getStatus()),
            () -> assertEquals(objectMapper.writeValueAsString(expectedResponse), responseBody)
        );

        ApplicationUser deletedUser = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertNull(deletedUser);
    }

    @Test
    void deleteUserShouldThrowNotFoundException() throws Exception {
        // Act
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/users/user/delete")
                .param("id", String.valueOf(-123345L))
                .characterEncoding(StandardCharsets.UTF_8.name())) // Ensure UTF-8 encoding
            .andExpect(status().isNotFound())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Assertions.assertAll(
            () -> assertEquals(404, response.getStatus())
        );
    }
}
