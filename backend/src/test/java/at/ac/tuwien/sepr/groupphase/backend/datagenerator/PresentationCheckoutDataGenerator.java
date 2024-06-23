package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile("generateData")
@Order(9999)
@Component
public class PresentationCheckoutDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(PresentationCheckoutDataGenerator.class);
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;
    @Autowired
    private HallSectorRepository hallSectorRepository;



    @PostConstruct
    protected void generateData() throws ForbiddenException, ValidationException {
        Random random = new Random(5);
        List<HallPlan> all = hallPlanRepository.findHallPlansByNameContainingIgnoreCase("open air", Pageable.unpaged());
        var openAir = all.get(0);
        Address address = new Address();
        address.setStreet("Roland-Rainer-Platz 1");
        address.setZip("1150");
        address.setCity("Wien");
        address.setCountry("Österreich");
        addressRepository.saveAndFlush(address);

        Location location = new Location();
        location.setName("Stadthalle Wien");
        location.setAddress(address);
        location.setHallPlan(openAir);
        locationRepository.saveAndFlush(location);

        Event event = new Event();
        event.setTitle("Green Day Konzert");
        // short desription with two sentences
        event.setDescription("Green Day is an American rock band formed in the East Bay of California in 1987 by lead vocalist and guitarist Billie Joe Armstrong and bassist and backing vocalist Mike Dirnt. For much of the band's career, they have been a trio with drummer Tré Cool, who replaced John Kiffmeyer in 1990 prior to the recording of the band's second studio album, Kerplunk (1991).");
        event.setEventType(EventType.CONCERT);
        event.setDuration(60 * 2L + 30L);

        eventRepository.save(event);

        List<Artist> allArtists = artistRepository.findAll(); // Assuming you have a method to fetch all artists

        // Randomly assign artists to the show
        List<Artist> assignedArtists = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            assignedArtists.add(allArtists.get(random.nextInt(allArtists.size())));
        }


        LocalDateTime dateTime = LocalDateTime.now().plusDays(7);
        Show show = new Show();
        show.setDateTime(dateTime);
        show.setArtists(assignedArtists);
        show.setEvent(event);
        show.setLocation(location);

        show = showRepository.saveAndFlush(show);

        List<HallSector> sectors = hallSectorRepository.findSectorsByHallPlanIds(List.of(openAir.getId()));
        for (HallSector sector : sectors) {
            var hss = new HallSectorShow(show, sector, random.nextInt(50, 1000));
            hallSectorShowRepository.save(hss);
        }

    }
}

