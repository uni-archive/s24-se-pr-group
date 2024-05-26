package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import com.github.javafaker.Faker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventSupplier {
    public static Event anEvent(Faker faker) {
        var event = new Event();
        event.setEventType(EventType.values()[faker.random().nextInt(0, EventType.values().length - 1)]);
        event.setTitle(faker.book().title());
        event.setDescription(faker.weather().description());
        event.setDuration((long) ((Math.random() + 60) * 60));
        return event;
    }

    public static List<Event> manyEvents(Faker faker, int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> anEvent(faker))
            .collect(Collectors.toList());
    }
}
