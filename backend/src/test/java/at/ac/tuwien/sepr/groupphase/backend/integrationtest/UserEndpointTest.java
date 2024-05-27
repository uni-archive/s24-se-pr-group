package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateInfoRequest;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
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

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES_2;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER_2;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

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
            () -> Assertions.assertEquals(403, response.getStatus()),
            () -> Assertions.assertEquals("User is already logged in", response.getContentAsString())
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
            () -> Assertions.assertEquals(403, response.getStatus()),
            () -> Assertions.assertEquals("User is not an admin", response.getContentAsString())
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
            () -> Assertions.assertEquals(201, response.getStatus())
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
            () -> Assertions.assertEquals(201, response.getStatus())
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
            new TypeReference<Page<ApplicationUserResponse>>() {
            });
        Assertions.assertAll(
            () -> Assertions.assertNotNull(users),
            () -> Assertions.assertEquals(1, users.getTotalElements()),
            () -> Assertions.assertEquals(1, users.getTotalPages())
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

        // Perform the PUT request with a admin token
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
    @WithMockUser(roles = "USER", username = "admin2@email.com")
    void updateUserInfoShouldUpdatePhoneNummer() throws Exception {
        // Arrange
        ApplicationUser user = ApplicationUserSupplier.anAdminUserEntity();
        user.setAddress(AddressSupplier.anAddressEntity());
        addressRepository.save(user.getAddress());
        userRepository.save(user);

        UserUpdateInfoRequest updateRequest = new UserUpdateInfoRequest(
            user.getId(), null,
            "+431234567890"
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
        Assertions.assertEquals("+431234567890", applicationUserResponse.phoneNumber());

        userRepository.delete(user);
    }

    @Test
    void updateUserEmailWithValidTokenShouldReturnBadRequestForInvalidToken() throws Exception {
        // Arrange
        String invalidToken = "invalidToken";

        // Act
        MvcResult mvcResult = mockMvc.perform(get(TestData.USER_BASE_URI + "/update/user/email")
                .param("token", invalidToken))
            .andExpect(status().isBadRequest())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertEquals("Dieser Link ist nicht gültig.", response.getContentAsString());
    }
}
