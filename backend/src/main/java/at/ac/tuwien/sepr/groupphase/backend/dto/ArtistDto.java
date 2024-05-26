package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.util.Objects;

public class ArtistDto implements AbstractDto {
    private Long id;
    private String artistName;
    private String firstName;
    private String lastName;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ArtistDto(String artistName, String firstName, String lastName) {
        this.artistName = artistName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ArtistDto() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArtistDto artistDto)) {
            return false;
        }
        return Objects.equals(artistName, artistDto.artistName) && Objects.equals(firstName, artistDto.firstName)
            && Objects.equals(lastName, artistDto.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName, firstName, lastName);
    }

    @Override
    public String toString() {
        return "ArtistDto{" + "artistName='" + artistName + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + '}';
    }


}
