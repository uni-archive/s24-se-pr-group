package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    @Test
    void createOrderShouldSetCookie() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            )
            .andExpect(status().isCreated())
            .andExpect(result -> {
                StringUtils.hasText(result.getResponse().getCookie("order").getValue());
            })
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        OrderDetailsResponse createdOrder = objectMapper.readValue(response.getContentAsString(),
            OrderDetailsResponse.class);
        Assertions.assertAll(
            () -> Assertions.assertNotNull(createdOrder),
            () -> Assertions.assertNotNull(createdOrder.id())
        );
    }

    @Test
    void createOrderShouldThrowUnauthorizedIfInvalidUserInToken() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("abc", ADMIN_ROLES))
            )
            .andExpect(status().isUnauthorized());
    }



}
