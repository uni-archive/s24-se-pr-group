package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import java.util.Objects;

@Entity
public class Artist extends AEntity{
    @Column(name = "ARTIST_NAME")
    private String artistName;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @ManyToMany
    @JoinTable(name = "ARTIST_SHOW",
        joinColumns = @JoinColumn(name = "ARTIST_ID"),
        inverseJoinColumns = @JoinColumn(name = "SHOW_ID"))
    private List<Show> shows;

    public Artist(String artistName, String firstName, String lastName, List<Show> shows) {
        this.artistName = artistName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.shows = shows;
    }

    public Artist() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(artistName, artist.artistName) && Objects.equals(firstName, artist.firstName) && Objects.equals(lastName, artist.lastName) && Objects.equals(shows, artist.shows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName, firstName, lastName, shows);
    }

    @Override
    public String toString() {
        return "Artist{" +
            "artistName='" + artistName + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", shows=" + shows +
            '}';
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}
