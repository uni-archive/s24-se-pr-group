package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.DevelopmentApplication;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void registerUserShouldResultInNewUser() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("abcd@cd.de", "password", "Peter", "Test",
            "+43 6776182783", false);

        mockMvc.perform(post(TestData.USER_BASE_URI + "/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isCreated());

        ApplicationUser user = userRepository.findByEmail("abcd@cd.de");
        Assertions.assertNotNull(user);
    }

    @Test
    void registerUserShouldNotAllowToCreateUserIfAlreadyRegistered() throws Exception {
        UserCreateRequest userCreateRequest = new UserCreateRequest("cdef@ge.ck", "password", "Peter", "Test",
            "+43 6776182783", false);

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
            "+43 6776182783", true);

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
            "+43 6776182783", true);

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
            "+43 6776182783", false);

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
}
