package at.ac.tuwien.sepr.groupphase.backend.unittests;

import static org.junit.jupiter.api.Assertions.*;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.invoke.MethodHandles;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventCreateTest {

    @Autowired
    EventService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testEventCreate(){
        int currentAmount = service.getAllEvents().size();
        EventDto eventdto = new EventDto(null, "EventTest", 60, "Cool event WOW!", EventType.CONCERT, List.of());
        service.createEvent(eventdto);
        assertEquals(currentAmount+1, service.getAllEvents().size());
    }
}
