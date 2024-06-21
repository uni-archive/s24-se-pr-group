package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeAddress;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyEvents;

@Profile("generateData")
@Component
public class CreateShowDataGenerator {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private HallSectorRepository hallSectorRepository;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    private void generateData() {
        var event = new Event();
        event.setEventType(EventType.PLAY);
        event.setTitle("create-show-test-event-title-001");
        event.setDescription("create-show-test-event-description-001");
        event.setDuration((long) ((Math.random() + 60) * 60));

        var seat1 = new HallSeat();
        var seat2 = new HallSeat();
        var seat3 = new HallSeat();

        var s1 = new HallSector();
        s1.setName("create-show-test-hallsector-001");
        s1.setSeats(List.of(seat1));
        seat1.setSector(s1);

        var s2 = new HallSector();
        s2.setName("create-show-test-hallsector-002");
        s2.setSeats(List.of(seat2));
        seat2.setSector(s2);

        var s3 = new HallSector();
        s3.setName("create-show-test-hallsector-003");
        s3.setSeats(List.of(seat3));
        seat3.setSector(s3);

        var sectors = List.of(s1, s2, s3);

        var hallPlan = new HallPlan();
        hallPlan.setName("create-show-test-hallplan-001");
        hallPlan.setSectors(sectors);
        sectors.forEach(s -> s.setHallPlan(hallPlan));

        var addr = fakeAddress();

        var loc = new Location();
        loc.setAddress(addr);
        loc.setName("create-show-test-location-name-001");
        loc.setHallPlan(hallPlan);

        addressRepository.save(addr);
        eventRepository.save(event);
        hallPlanRepository.save(hallPlan);
        locationRepository.save(loc);
        hallSectorRepository.saveAll(sectors);
        hallSpotRepository.saveAll(List.of(seat1, seat2, seat3));

        var artist = new Artist();
        artist.setArtistName("create-show-test-artist-artistname-001");
        artist.setFirstName("create-show-test-artist-firstname-001");
        artist.setLastName("create-show-test-artist-lastname-001");
        artistRepository.save(artist);
    }
}
