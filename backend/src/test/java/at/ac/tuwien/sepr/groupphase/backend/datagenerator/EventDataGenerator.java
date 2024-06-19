package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("generateData")
@Component
public class EventDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(EventDataGenerator.class);
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        Faker faker = new Faker();
        Random random = new Random();

        EventType[] eventTypes = EventType.values(); // Assuming EventType is an enum

        for (int i = 1; i <= dataGenerationConfig.eventAmount; i++) {
            log.info("Generating event " + i + "/" + dataGenerationConfig.eventAmount);
            String title = faker.book().title();
            String description = faker.lorem().paragraph();
            EventType eventType = eventTypes[random.nextInt(eventTypes.length)];
            Long duration = (long) (random.nextInt(3 * 60 * 60) + 1); // Duration between 1 second and 3 hours

            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);
            event.setEventType(eventType);
            event.setDuration(duration);

            eventRepository.save(event);
        }
    }
}
