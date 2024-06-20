package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ShowCreateTest {

    @Autowired
    private ShowService service;

    @Autowired
    private EventService eventService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // @Test
    public void testShowCreate() {
        EventDto eventdto = new EventDto(null, "EventTest", 60, "Cool event WOW!", EventType.CONCERT, List.of());
        eventService.createEvent(eventdto);

        service.createShow(new ShowDto(null, LocalDate.of(2020, Month.JANUARY, 18).atStartOfDay(), null,null,null),
            List.of());

        assertEquals(1, service.getAllShows().size());
    }

}
