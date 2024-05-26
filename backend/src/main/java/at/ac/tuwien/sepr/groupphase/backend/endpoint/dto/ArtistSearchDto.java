package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record ArtistSearchDto(
    String firstName,
    String lastName,
    String artistName
)  {
}
