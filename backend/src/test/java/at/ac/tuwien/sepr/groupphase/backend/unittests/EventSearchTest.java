package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.EventGenerator;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventSearchTest {

    @Autowired
    EventService service;

    @Autowired
    EventRepository repository;

    @Autowired
    EventMapper mapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testSearch() {
        for (int i = 0; i < 100; i++) {
            Event event = new Event();

            switch (i%3){
                case 0:
                    event.setEventType(EventType.PLAY);
                    break;
                case 1:
                    event.setEventType(EventType.CONCERT);
                    break;
                case 2:
                    event.setEventType(EventType.THEATER);
                    break;
            }
            repository.save(event
                .setDescription("Event number " + i)
                .setDuration((long) i*60)
                .setTitle("EventName"+ i));
            //LOGGER.info("Saving: {}", event);
        }

        assertEquals(1, service.searchEvents(new EventSearchDto(3,EventType.PLAY,"3")).size());
        assertEquals(5, service.searchEvents(new EventSearchDto(10, null, "")).size());

    }

}
