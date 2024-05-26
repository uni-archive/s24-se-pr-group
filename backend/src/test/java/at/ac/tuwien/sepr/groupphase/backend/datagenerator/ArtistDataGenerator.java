package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("generateData")
@Component
public class ArtistDataGenerator {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ShowRepository showRepository;

    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        Faker faker = new Faker();

        for (int i = 1; i <= 40; i++) {
            String artistName = faker.artist().name();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();

            Artist artist = new Artist();
            artist.setArtistName(artistName);
            artist.setFirstName(firstName);
            artist.setLastName(lastName);
            artist.setShows(List.of());

            artistRepository.save(artist); // Assuming you have a method to create an artist
        }
    }
}
