package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreationRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class TicketEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HallSectorRepository hallSectorRepository;

    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private EventRepository eventRepository;

    private static Show show;
    private static Event event;
    private static HallPlan hallPlan;
    private static HallSpot hallSpot;
    private static HallSpot hallSpot2;


    @BeforeEach
    void setup() {
        event = new Event();
        show = new Show();
        show.setEvent(event);
        show.setDateTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        event.setShows(List.of(show));
        event = eventRepository.save(event);
        show = showRepository.save(show);

        hallPlan = new HallPlan();
        hallPlanRepository.save(hallPlan);

        var sector = new HallSector();
        sector.setHallPlan(hallPlan);
        hallSectorRepository.save(sector);

        hallSpot = new HallSpot();
        hallSpot.setSector(sector);
        hallSpot = hallSpotRepository.save(hallSpot);

        hallSpot2 = new HallSpot();
        hallSpot2.setSector(sector);
        hallSpot2 = hallSpotRepository.save(hallSpot2);

        var hallSpot3 = new HallSpot();
        hallSpot3.setSector(sector);
        hallSpotRepository.save(hallSpot3);

        var hallSpot4 = new HallSpot();
        hallSpot4.setSector(sector);
        hallSpotRepository.save(hallSpot4);

        var hallSpot5 = new HallSpot();
        hallSpot5.setSector(sector);
        hallSpotRepository.save(hallSpot5);

        var hallSpot6 = new HallSpot();
        hallSpot6.setSector(sector);
        hallSpotRepository.save(hallSpot6);

        var sectorShow = new HallSectorShow();
        sectorShow.setSector(sector);
        sectorShow.setShow(show);
        sectorShow.setPrice(50);
        hallSectorShowRepository.save(sectorShow);
    }

    @Test
    void addTicketShouldCreateTicket() throws Exception {
        Long orderId = createOrder();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tickets")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new TicketCreationRequest(
                        hallSpot.getId(), orderId,
                        show.getId(), false))
                ))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TicketDetailsResponse createdTicket = objectMapper.readValue(response.getContentAsString(),
            TicketDetailsResponse.class);
        Assertions.assertAll(
            () -> Assertions.assertNotNull(createdTicket),
            () -> Assertions.assertNotNull(createdTicket.id()),
            () -> Assertions.assertNotNull(createdTicket.hash()),
            () -> Assertions.assertFalse(createdTicket.reserved()),
            () -> Assertions.assertFalse(createdTicket.valid()),
            () -> Assertions.assertNotNull(createdTicket.show()),
            () -> Assertions.assertNotNull(createdTicket.hallSpot())
        );

        Assertions.assertTrue(ticketRepository.findTicketsByUserId(userRepository.findByEmail(ADMIN_USER).getId())
            .stream()
            .anyMatch(ticket -> ticket.getId().equals(createdTicket.id())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addTicketShouldCreateReservedTicket() throws Exception {
        Long orderId = createOrder();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tickets")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new TicketCreationRequest(
                        hallSpot2.getId(), orderId,
                        show.getId(), true))
                ))
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        TicketDetailsResponse createdTicket = objectMapper.readValue(response.getContentAsString(),
            TicketDetailsResponse.class);
        Assertions.assertAll(
            () -> Assertions.assertNotNull(createdTicket),
            () -> Assertions.assertNotNull(createdTicket.id()),
            () -> Assertions.assertNotNull(createdTicket.hash()),
            () -> Assertions.assertTrue(createdTicket.reserved()),
            () -> Assertions.assertFalse(createdTicket.valid()),
            () -> Assertions.assertNotNull(createdTicket.show()),
            () -> Assertions.assertNotNull(createdTicket.hallSpot())
        );

        Assertions.assertTrue(ticketRepository.findTicketsByUserId(userRepository.findByEmail(ADMIN_USER).getId())
            .stream()
            .anyMatch(ticket -> ticket.getId().equals(createdTicket.id())));
    }

    private Long createOrder() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            )
            .andExpect(status().isCreated())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        OrderDetailsResponse createdOrder = objectMapper.readValue(response.getContentAsString(),
            OrderDetailsResponse.class);
        return createdOrder.id();
    }
}