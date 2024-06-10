package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class ShowDataGenerator {

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

    @Transactional
    @PostConstruct
    protected void generateData() throws ForbiddenException, ValidationException {
        Random random = new Random();
        List<Artist> allArtists = artistRepository.findAll(); // Assuming you have a method to fetch all artists
        List<Event> allEvents = eventRepository.findAll(); // Assuming you have a method to fetch all events
        List<Location> allLocations = locationRepository.findAllWithHallplan(); // Assuming you have a method to fetch all locations

        for (int i = 1; i <= 40; i++) {
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

            showRepository.saveAndFlush(show);

            for (HallSector sector : location.getHallPlan().getSectors()) {
                var hss = new HallSectorShow(show, sector, random.nextInt(50, 1000));
                hallSectorShowRepository.save(hss);
            }
        }
    }
}
