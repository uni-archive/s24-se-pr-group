package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class ShowDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(ShowDataGenerator.class);
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;

    @Autowired
    private LocationDataGenerator locationDataGenerator;

    @Autowired
    private ArtistDataGenerator artistDataGenerator;

    @Autowired
    private EventDataGenerator eventDataGenerator;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;


    @Transactional
    @PostConstruct
    protected void generateData() throws ForbiddenException, ValidationException {
        Random random = new Random();
        List<Artist> allArtists = artistRepository.findAll();
        List<Event> allEvents = eventRepository.findAll();
        List<Location> allLocations = locationRepository.findAllWithHallplan();

        for (int i = 1; i <= dataGenerationConfig.showAmount; i++) {
            log.info("Generating show " + i + " of " + dataGenerationConfig.showAmount);
            LocalDateTime dateTime = LocalDateTime.now().plusDays(random.nextInt(365));

            // Randomly assign artists to the show
            List<Artist> assignedArtists = new ArrayList<>();
            for (int j = 0; j < random.nextInt(allArtists.size()); j++) {
                assignedArtists.add(allArtists.get(random.nextInt(allArtists.size())));
            }

            Event event = allEvents.get(random.nextInt(allEvents.size()));
            Location location = allLocations.get(random.nextInt(allLocations.size()));

            Show show = new Show();
            show.setDateTime(dateTime);
            show.setArtists(assignedArtists);
            show.setEvent(event);
            show.setLocation(location);

            show = showRepository.saveAndFlush(show);

            List<HallSector> sectors = location.getHallPlan().getSectors().stream().distinct().toList();
            for (HallSector sector : sectors) {
                var hss = new HallSectorShow(show, sector, random.nextInt(500, 100000));
                hallSectorShowRepository.save(hss);
            }
        }
    }
}
