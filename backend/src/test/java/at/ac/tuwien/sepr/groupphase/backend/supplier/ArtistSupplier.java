package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import com.github.javafaker.Faker;

public class ArtistSupplier {
    public static Artist anArtist(Faker faker) {
        var artist = new Artist();
        artist.setArtistName(faker.artist().name());
        artist.setFirstName(faker.name().firstName());
        artist.setLastName(faker.name().lastName());
        return artist;
    }

    public static Artist anArtist() {
        var artist = new Artist();
        artist.setFirstName("Artist-Firstname");
        artist.setLastName("Artist-Lastname");
        artist.setArtistName("Artist-ArtistName");
        return artist;
    }
}
