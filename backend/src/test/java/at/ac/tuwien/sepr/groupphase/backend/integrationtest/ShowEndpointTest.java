package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.DEFAULT_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
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
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShowEndpointTest {

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
    void getByLocationShouldOnlyReturnShowsAtLocation() throws Exception {
        // Create test data
        Location location1 = new Location("Test Location 1",
            addressRepository.saveAndFlush(AddressSupplier.anAddressEntity()));
        Location location2 = new Location("Test Location 2",
            addressRepository.saveAndFlush(AddressSupplier.anAddressEntity()));
        locationRepository.saveAndFlush(location1);
        locationRepository.saveAndFlush(location2);

        LocalDateTime expectedTime = LocalDateTime.now().plus(8, ChronoUnit.DAYS);
        Show show1 = new Show(expectedTime, List.of(), null, location1, List.of());
        Show show2 = new Show(LocalDateTime.now().plus(9, ChronoUnit.DAYS), List.of(), null, location2, List.of());
        showRepository.saveAndFlush(show1);
        showRepository.saveAndFlush(show2);

        // Perform request
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/show/location/" + location1.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        // Check response
        MockHttpServletResponse response = mvcResult.getResponse();
        Page<ShowResponse> foundShows = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<Page<ShowResponse>>() {
            });

        Assertions.assertAll(
            () -> Assertions.assertEquals(1, foundShows.getTotalElements()),
            () -> Assertions.assertEquals(1, foundShows.getTotalPages()),
            () -> Assertions.assertEquals(expectedTime.toEpochSecond(ZoneOffset.UTC),
                foundShows.getContent().get(0).dateTime().toEpochSecond(ZoneOffset.UTC))
        );
    }
}
