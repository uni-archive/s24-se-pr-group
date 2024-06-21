package at.ac.tuwien.sepr.groupphase.backend.dto;

import org.springframework.data.domain.Pageable;

public record ArtistSearchDto(
    String firstName,
    String lastName,
    String artistName,

    Pageable pageable
)  {
    public ArtistSearchDto(String firstName, String lastName, String artistName) {
        this(firstName, lastName, artistName, null);
    }
}
