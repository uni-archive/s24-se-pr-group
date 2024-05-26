package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class EventGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private EventRepository eventRepository;

    public EventGenerator(EventRepository repo) {
        this.eventRepository = repo;
    }


    @PostConstruct
    public void generateData() {
        /*
        for (int i = 0; i < 100; i++) {
            switch (i % 4) {
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

            eventRepository.save(event
                .setDescription("Event number " + i)
                .setDuration((long) i)
                .setTitle("EventName" + i));
        } */
    }

}
