package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class EventEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    public void setup() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        eventRepository.deleteAll();

        for (int i = 1; i <= 12; i++) {
            Event event = new Event();
            event.setTitle("Event " + i);
            event.setDescription("Description " + i);
            eventRepository.save(event);

            Show show = new Show();
            show.setEvent(event);
            show.setDateTime(LocalDateTime.now().minusDays(10 - i));
            showRepository.save(show);

            for (int j = 0; j < (i <= 2 ? 1 : i * 2); j++) {
                Ticket ticket = new Ticket();
                ticket.setShow(show);
                ticketRepository.save(ticket);
            }
        }
    }

    @Test
    public void testGetTop10EventsWithMostTicketsShouldReturnTop10() throws Exception {
        mockMvc.perform(get("/api/v1/events/top10").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$[0].title").value("Event 12"))
            .andExpect(jsonPath("$[0].ticketCount").value(24))
            .andExpect(jsonPath("$[1].title").value("Event 11"))
            .andExpect(jsonPath("$[1].ticketCount").value(22))
            .andExpect(jsonPath("$[2].title").value("Event 10"))
            .andExpect(jsonPath("$[2].ticketCount").value(20))
            .andExpect(jsonPath("$[3].title").value("Event 9"))
            .andExpect(jsonPath("$[3].ticketCount").value(18))
            .andExpect(jsonPath("$[4].title").value("Event 8"))
            .andExpect(jsonPath("$[4].ticketCount").value(16))
            .andExpect(jsonPath("$[5].title").value("Event 7"))
            .andExpect(jsonPath("$[5].ticketCount").value(14))
            .andExpect(jsonPath("$[6].title").value("Event 6"))
            .andExpect(jsonPath("$[6].ticketCount").value(12))
            .andExpect(jsonPath("$[7].title").value("Event 5"))
            .andExpect(jsonPath("$[7].ticketCount").value(10))
            .andExpect(jsonPath("$[8].title").value("Event 4"))
            .andExpect(jsonPath("$[8].ticketCount").value(8))
            .andExpect(jsonPath("$[9].title").value("Event 3"))
            .andExpect(jsonPath("$[9].ticketCount").value(6))
            .andExpect(jsonPath("$[?(@.title == 'Event 1')]").doesNotExist())
            .andExpect(jsonPath("$[?(@.title == 'Event 2')]").doesNotExist());
    }
}
