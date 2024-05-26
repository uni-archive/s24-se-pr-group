package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ArtistSupplier.anArtist;
import static at.ac.tuwien.sepr.groupphase.backend.supplier.EventSupplier.anEvent;
import static at.ac.tuwien.sepr.groupphase.backend.supplier.ShowSupplier.manyShows;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class EventServiceImplTest {
    @Autowired
    private EventServiceImpl eventService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventDao eventDao;

    private static ArtistDto artistWithEvents1;
    private static List<EventDto> eventsForArtist1;
    private static ArtistDto artistWithEvents2;
    private static List<EventDto> eventsForArtist2;
    private static ArtistDto artistWithNoEvents;

    private static final Faker faker = new Faker();

    @BeforeEach
    void setup() {
        var artist1 = anArtist(faker);
        var artist2 = anArtist(faker);
        var artist3 = anArtist(faker);

        var shows = manyShows(faker, 30);
        var showsForArtist1 = shows.subList(0, 10);
        var showsForArtist2 = shows.subList(10, 20);
        var showsForArtist1And2 = shows.subList(20, 30);

        var eventWithNoShows = anEvent(faker);
        var eventForArtist1 = anEvent(faker);
        var eventForArtist1And2 = anEvent(faker);
        var eventForArtist2 = anEvent(faker);
        var events = List.of(eventForArtist1, eventForArtist2, eventForArtist1And2, eventWithNoShows);

        artist1.setShows(new ArrayList<>() {{
                addAll(showsForArtist1);
                addAll(showsForArtist1And2);
            }}
        );


        artist2.setShows(new ArrayList<>() {{
                addAll(showsForArtist2);
                addAll(showsForArtist1And2);
            }}
        );

        showsForArtist1.forEach(s -> s.setArtists(List.of(artist1)));
        showsForArtist2.forEach(s -> s.setArtists(List.of(artist2)));
        showsForArtist1And2.forEach(s -> s.setArtists(List.of(artist1, artist2)));

        eventForArtist1.setShows(showsForArtist1);
        eventForArtist2.setShows(showsForArtist2);
        eventForArtist1And2.setShows(showsForArtist1And2);

        showsForArtist1.forEach(s -> s.setEvent(eventForArtist1));
        showsForArtist2.forEach(s -> s.setEvent(eventForArtist2));
        showsForArtist1And2.forEach(s -> s.setEvent(eventForArtist1And2));

        eventRepository.saveAll(events);
        showRepository.saveAll(shows);
        artistRepository.saveAll(List.of(artist1, artist2, artist3));

        artistWithEvents1 = artistMapper.toDto(artist1);
        artistWithEvents2 = artistMapper.toDto(artist2);
        artistWithNoEvents = artistMapper.toDto(artist3);

        eventsForArtist1 = eventMapper.toDto(List.of(eventForArtist1, eventForArtist1And2));
        eventsForArtist2 = eventMapper.toDto(List.of(eventForArtist2, eventForArtist1And2));
    }

    @AfterEach
    void teardown() {
        artistRepository.deleteAll();
        showRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void searching_eventsForAnArtist_returnsOnlyTheEventsForThatArtist() {
        assertThat(eventService.findByArtist(artistWithEvents1.getId()))
            .isNotNull()
            .usingRecursiveComparison()
            .comparingOnlyFields("id", "description", "duration", "title", "type")
            .isEqualTo(eventsForArtist1);

        assertThat(eventService.findByArtist(artistWithEvents2.getId()))
            .isNotNull()
            .usingRecursiveComparison()
            .comparingOnlyFields("id", "description", "duration", "title", "type")
            .isEqualTo(eventsForArtist2);
    }

    @Test
    void searching_eventsForAnonExistentArtist_returnsAnEmptyList() {
        assertThat(eventService.findByArtist(artistWithNoEvents.getId()))
            .isNotNull()
            .hasSize(0);
    }

    @Test
    void searching_eventsForAnArtistWithNoEvents_returnsAnEmptyList() {
        assertThat(eventService.findByArtist(-1L))
            .isNotNull()
            .hasSize(0);
    }
}
