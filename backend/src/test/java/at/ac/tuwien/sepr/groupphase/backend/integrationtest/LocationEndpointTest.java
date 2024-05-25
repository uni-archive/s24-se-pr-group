package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.supplier.AddressSupplier;
import at.ac.tuwien.sepr.groupphase.backend.util.PageModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new PageModule());
    }

    @Test
    @Order(1)
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
    @Order(2)
    void updateShouldUpdateLocationAsUser() throws Exception {
        Address address = AddressSupplier.anAddressEntity();
        addressRepository.save(address);
        Location oldLocation = new Location("Old Location", address);
        oldLocation = locationRepository.save(oldLocation);

        LocationDto updateRequest = new LocationDto(oldLocation.getId(), "New Location")
            .setAddress(new AddressDto(address.getId(), "New Street", "New City", "1111", "New Country"));

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/location/" + oldLocation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
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
    @Order(3)
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
    @Order(4)
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
    @Order(5)
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
    @Order(6)
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
    @Order(7)
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

    @Test
    @Order(8)
    @DirtiesContext
    void searchForLocationsShouldReturnAllLocationsThatFitCriteria() throws Exception {
        locationRepository.deleteAll();
        for (int i = 0; i < 40; i++) {
            Address address;
            if (i % 2 == 0) {
                address = new Address("Test Street 1", "1010", "Vienna", "Austria");
            } else {
                address = new Address("Bergzeile 87", "3970", "Weitra", "Tschechien");
            }
            addressRepository.save(address);
            locationRepository.save(new Location("Location " + i, address));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "0")
                .param("size", "15")
                .param("sort", "name"))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<LocationResponse> locations = objectMapper.readValue(response.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });

        Assertions.assertAll(
            () -> Assertions.assertEquals(15, locations.getNumberOfElements()),
            () -> Assertions.assertTrue(locations.stream().allMatch(location -> location.name().startsWith("Location"))),
            () -> Assertions.assertTrue(locations.stream().allMatch(location -> Objects.nonNull(location.address())))
        );

        MvcResult mvcResult2 = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "2")
                .param("size", "15")
                .param("sort", "name"))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();
        Page<LocationResponse> locations2 = objectMapper.readValue(response2.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });
        Assertions.assertAll(
            () -> Assertions.assertEquals(10, locations2.getNumberOfElements()),
            () -> Assertions.assertTrue(locations2.stream().allMatch(location -> location.name().startsWith("Location"))),
            () -> Assertions.assertTrue(locations2.stream().allMatch(location -> Objects.nonNull(location.address())))
        );
    }

    @Test
    @Order(9)
    @DirtiesContext
    void searchForLocationsShouldOnlyReturnResultsThatMatchName() throws Exception {
        for (int i = 0; i < 40; i++) {
            Address address;
            if (i % 2 == 0) {
                address = new Address("Test Street 1", "1010", "Vienna", "Austria");
            } else {
                address = new Address("Bergzeile 87", "3970", "Weitra", "Tschechien");
            }
            addressRepository.save(address);
            locationRepository.save(new Location("Location " + i, address));
        }
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "0")
                .param("size", "15")
                .param("sort", "name")
                .param("name", "Location 10"))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<LocationResponse> locations = objectMapper.readValue(response.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });

        Assertions.assertAll(
            () -> Assertions.assertEquals(1, locations.getNumberOfElements()),
            () -> Assertions.assertTrue(locations.stream().allMatch(location -> location.name().equals("Location 10")))
        );
    }

    @Test
    @Order(10)
    @DirtiesContext
    void searchForLocationsShouldOnlyReturnResultsThatMatchAddress() throws Exception {
        for (int i = 0; i < 40; i++) {
            Address address;
            if (i % 2 == 0) {
                address = new Address("Test Street 1", "1010", "Vienna", "Austria");
            } else {
                address = new Address("Bergzeile 87", "3970", "Weitra", "Tschechien");
            }
            addressRepository.save(address);
            locationRepository.save(new Location("Location " + i, address));
        }
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "0")
                .param("size", "15")
                .param("sort", "name")
                .param("street", "Bergzeile 87")
                .param("postalCode", "3970")
                .param("city", "Weitra")
                .param("country", "Tschechien")
            )
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<LocationResponse> locations = objectMapper.readValue(response.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });

        Assertions.assertAll(
            () -> Assertions.assertEquals(15, locations.getNumberOfElements()),
            () -> Assertions.assertEquals(20, locations.getTotalElements())
        );
    }

    @Test
    @Order(10)
    @DirtiesContext
    void searchForLocationsShouldOnlyReturnResultsThatAreSorted() throws Exception {
        for (int i = 40; i >= 1; i--) {
            Address address;
            if (i % 2 == 0) {
                address = new Address("Test Street 1", "1010", "Vienna", "Austria");
            } else {
                address = new Address("Bergzeile 87", "3970", "Weitra", "Tschechien");
            }
            addressRepository.save(address);
            locationRepository.save(new Location("Location " + i, address));
        }
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "0")
                .param("size", "15")
                .param("sort", "name")
                .param("name", "Location 1")
            )
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<LocationResponse> locations = objectMapper.readValue(response.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });

        Assertions.assertAll(
            () -> Assertions.assertEquals(11, locations.getNumberOfElements()),
            () -> Assertions.assertEquals("Location 1", locations.getContent().get(0).name()),
            () -> Assertions.assertEquals("Location 10", locations.getContent().get(1).name()),
            () -> Assertions.assertEquals("Location 11", locations.getContent().get(2).name()),
            () -> Assertions.assertEquals("Location 12", locations.getContent().get(3).name()),
            () -> Assertions.assertEquals("Location 13", locations.getContent().get(4).name()),
            () -> Assertions.assertEquals("Location 14", locations.getContent().get(5).name()),
            () -> Assertions.assertEquals("Location 15", locations.getContent().get(6).name()),
            () -> Assertions.assertEquals("Location 16", locations.getContent().get(7).name()),
            () -> Assertions.assertEquals("Location 17", locations.getContent().get(8).name()),
            () -> Assertions.assertEquals("Location 18", locations.getContent().get(9).name()),
            () -> Assertions.assertEquals("Location 19", locations.getContent().get(10).name())
        );
    }

    @Test
    @DirtiesContext
    void searchLocationShouldReturnOnlyLocationsThatHaveShowsInTheFuture() throws Exception {
        Address address = new Address("Test Street 1", "1010", "Vienna", "Austria");
        Address address2 = new Address("Test Street 2", "1010", "Vienna", "Austria");
        Address address3 = new Address("Test Street 3", "1010", "Vienna", "Austria");
        addressRepository.save(address);
        addressRepository.save(address2);
        addressRepository.save(address3);
        Location locationWithShowsInFuture = new Location("Location 1", address);
        Location locationWIthShowsInFutureAndPast = new Location("Location 2", address2);
        Location locationWithShowsInPast = new Location("Location 3", address3);
        Show showInFuture1 = new Show(LocalDateTime.now().plus(1, ChronoUnit.DAYS), List.of(), null, null, List.of());
        Show showInFuture2 = new Show(LocalDateTime.now().plus(2, ChronoUnit.DAYS), List.of(), null, null, List.of());
        Show showInPast1 = new Show(LocalDateTime.now().minus(1, ChronoUnit.DAYS), List.of(), null, null, List.of());
        Show showInPast2 = new Show(LocalDateTime.now().minus(2, ChronoUnit.DAYS), List.of(), null, null, List.of());
        showInFuture1 = showRepository.save(showInFuture1);
        showInFuture2 = showRepository.save(showInFuture2);
        showInPast1 = showRepository.save(showInPast1);
        showInPast2 = showRepository.save(showInPast2);
        locationWithShowsInFuture.setShows(List.of(showInFuture1));
        locationRepository.save(locationWithShowsInFuture);
        locationWIthShowsInFutureAndPast.setShows(List.of(showInPast1, showInFuture2));
        locationRepository.save(locationWIthShowsInFutureAndPast);
        locationWithShowsInPast.setShows(List.of(showInPast2));
        locationRepository.save(locationWithShowsInPast);
        showInFuture1.setLocation(locationWithShowsInFuture);
        showInFuture2.setLocation(locationWIthShowsInFutureAndPast);
        showInPast1.setLocation(locationWIthShowsInFutureAndPast);
        showInPast2.setLocation(locationWithShowsInPast);
        showRepository.save(showInFuture1);
        showRepository.save(showInFuture2);
        showRepository.save(showInPast1);
        showRepository.save(showInPast2);


        MvcResult mvcResult = mockMvc.perform(get("/api/v1/location/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("page", "0")
                .param("size", "15")
                .param("sort", "name")
                .param("withUpComingShows", "true")
            )
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Page<LocationResponse> locations = objectMapper.readValue(response.getContentAsString(), new TypeReference<Page<LocationResponse>>() {
        });

        Assertions.assertAll(
            ()-> Assertions.assertEquals(2, locations.getNumberOfElements()),
            ()-> Assertions.assertTrue(locations.stream().allMatch(location -> location.name().equals("Location 1") || location.name().equals("Location 2")))
        );
    }
}
