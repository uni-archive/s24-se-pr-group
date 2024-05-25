package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class AddressEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private LocationRepository locationRepository;

    @Test
    void createShouldCreateAddressAsAdmin() throws Exception {
        AddressCreateRequest createRequest = new AddressCreateRequest("123 Main St", "Vienna", "1010", "Austria");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/address")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        AddressDto createdAddress = objectMapper.readValue(response.getContentAsString(), AddressDto.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(createdAddress),
            () -> Assertions.assertEquals("123 Main St", createdAddress.getStreet()),
            () -> Assertions.assertEquals("Vienna", createdAddress.getCity()),
            () -> Assertions.assertEquals("1010", createdAddress.getZip()),
            () -> Assertions.assertEquals("Austria", createdAddress.getCountry())
        );
    }

    @Test
    void createInvalidAddressShouldReturnValidationErrors() throws Exception {
        AddressCreateRequest invalidCreateRequest = new AddressCreateRequest("", "", "not-a-number", "");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/address")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(invalidCreateRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseContent = response.getContentAsString();

        Assertions.assertAll(
            () -> Assertions.assertTrue(responseContent.contains("Street must not be empty")),
            () -> Assertions.assertTrue(responseContent.contains("City must not be empty")),
            () -> Assertions.assertTrue(responseContent.contains("Zip code must be a number"))
        );
    }

    @Test
    void updateShouldUpdateAddressAsUser() throws Exception {
        Address address = new Address("Old Street", "Old City", "0000", "Old Country");
        address = addressRepository.save(address);
        ApplicationUser byEmail = userRepository.findByEmail(DEFAULT_USER);
        byEmail.setAddress(address);
        userRepository.save(byEmail);

        AddressDto updateRequest = new AddressDto(null, "New Street", "New City", "1111", "New Country");

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/address/" + address.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        AddressDto updatedAddress = objectMapper.readValue(response.getContentAsString(), AddressDto.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(updatedAddress),
            () -> Assertions.assertEquals("New Street", updatedAddress.getStreet()),
            () -> Assertions.assertEquals("New City", updatedAddress.getCity()),
            () -> Assertions.assertEquals("1111", updatedAddress.getZip()),
            () -> Assertions.assertEquals("New Country", updatedAddress.getCountry())
        );
    }

    @Test
    void deleteShouldDeleteAddressAsAdmin() throws Exception {
        Address address = new Address("Street to Delete", "City to Delete", "9999", "Country to Delete");
        address = addressRepository.save(address);

        mockMvc.perform(delete("/api/v1/address/" + address.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent())
            .andReturn();

        Assertions.assertFalse(addressRepository.findById(address.getId()).isPresent());
    }

    @Test
    void findByIdShouldReturnAddressAsUser() throws Exception {
        Address address = new Address("Street to Find", "8888", "City to Find", "Country to Find");
        address = addressRepository.save(address);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/address/" + address.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        AddressDto foundAddress = objectMapper.readValue(response.getContentAsString(), AddressDto.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(foundAddress),
            () -> Assertions.assertEquals("Street to Find", foundAddress.getStreet()),
            () -> Assertions.assertEquals("City to Find", foundAddress.getCity()),
            () -> Assertions.assertEquals("8888", foundAddress.getZip()),
            () -> Assertions.assertEquals("Country to Find", foundAddress.getCountry())
        );
    }

    @Test
    void findAllShouldReturnAllAddressesAsAdmin() throws Exception {
        int size = addressRepository.findAll().size();
        addressRepository.save(new Address("Street 1", "City 1", "1111", "Country 1"));
        addressRepository.save(new Address("Street 2", "City 2", "2222", "Country 2"));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/address")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        List<AddressDto> addresses = objectMapper.readValue(response.getContentAsString(), List.class);

        Assertions.assertEquals(size+2, addresses.size());
    }
}
