package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.LocationEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.supplier.AddressSupplier;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LocationEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private LocationEndpoint locationEndpoint;

    @Test
    void createShouldCreateLocationAsAdmin() throws Exception {
        AddressCreateRequest addressCreateRequest = new AddressCreateRequest("123 Main St", "Vienna", "1010",
            "Austria");
        LocationCreateRequest createRequest = new LocationCreateRequest("Test Location", addressCreateRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        LocationDto createdLocation = objectMapper.readValue(response.getContentAsString(), LocationDto.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(createdLocation),
            () -> Assertions.assertEquals("Test Location", createdLocation.getName()),
            () -> Assertions.assertEquals("123 Main St", createdLocation.getAddress().getStreet()),
            () -> Assertions.assertEquals("Vienna", createdLocation.getAddress().getCity()),
            () -> Assertions.assertEquals("1010", createdLocation.getAddress().getZip()),
            () -> Assertions.assertEquals("Austria", createdLocation.getAddress().getCountry())
        );
    }

    @Test
    void updateShouldUpdateLocationAsUser() throws Exception {
        Address address = AddressSupplier.anAddressEntity();
        addressRepository.save(address);
        Location oldLocation = new Location("Old Location", address);
        oldLocation = locationRepository.save(oldLocation);

        LocationDto updateRequest = new LocationDto(oldLocation.getId(), "New Location")
            .setAddress(new AddressDto(address.getId(), "New Street", "New City", "1111", "New Country"));

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/location/" + oldLocation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        LocationDto updatedLocation = objectMapper.readValue(response.getContentAsString(), LocationDto.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(updatedLocation),
            () -> Assertions.assertEquals("New Location", updatedLocation.getName())
        );
    }

    @Test
    void deleteShouldDeleteLocationAsAdmin() throws Exception {
        Address address = AddressSupplier.anAddressEntity();
        addressRepository.save(address);
        Location location = new Location("Location to Delete", address);
        location = locationRepository.save(location);

        mockMvc.perform(delete("/api/v1/location/" + location.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isNoContent())
            .andReturn();

        Assertions.assertFalse(locationRepository.findById(location.getId()).isPresent());
    }

    @Test
    void findByIdShouldReturnLocationAsUser() throws Exception {
        Address address = AddressSupplier.anAddressEntity();
        Address savedAddress = addressRepository.save(address);

        Location location = new Location("Location to Find", address);
        location = locationRepository.save(location);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/" + location.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        LocationResponse foundLocation = objectMapper.readValue(response.getContentAsString(), LocationResponse.class);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(foundLocation),
            () -> Assertions.assertEquals("Location to Find", foundLocation.name()),
            () -> Assertions.assertEquals(savedAddress.getStreet(), foundLocation.address().street()),
            () -> Assertions.assertEquals(savedAddress.getCity(), foundLocation.address().city()),
            () -> Assertions.assertEquals(savedAddress.getZip(), foundLocation.address().zip()),
            () -> Assertions.assertEquals(savedAddress.getCountry(), foundLocation.address().country())
        );
    }

    @Test
    void findAllShouldReturnAllLocationsAsAdmin() throws Exception {

        locationRepository.deleteAll();
        Address address = AddressSupplier.anAddressEntity();
        Address address2 = AddressSupplier.anAddressEntity();
        address2.setStreet("Roßauer Lände 12");
        addressRepository.save(address);
        addressRepository.save(address2);
        locationRepository.save(new Location("Location 1", address));
        locationRepository.save(new Location("Location 2", address2));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        List<LocationDto> locations = objectMapper.readValue(response.getContentAsString(), List.class);

        Assertions.assertEquals(2, locations.size());
    }

    @Test
    void createInvalidLocationShouldReturnValidationErrors() throws Exception {
        LocationCreateRequest invalidCreateRequest = new LocationCreateRequest("", null);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .content(objectMapper.writeValueAsString(invalidCreateRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseContent = response.getContentAsString();

        Assertions.assertAll(
            () -> Assertions.assertTrue(responseContent.contains("Name is required")),
            () -> Assertions.assertTrue(responseContent.contains("Address is required"))
        );
    }

    @Test
    void createInvalidAddressShouldReturnValidationErrors() throws Exception {
        AddressCreateRequest invalidAddressCreateRequest = new AddressCreateRequest("", "", "not-a-number", "");
        LocationCreateRequest invalidCreateRequest = new LocationCreateRequest("locationname",
            invalidAddressCreateRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/location")
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
}
