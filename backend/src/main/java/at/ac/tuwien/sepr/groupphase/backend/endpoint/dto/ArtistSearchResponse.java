package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record ArtistSearchResponse(
    Long id,
    String firstName,
    String lastName,
    String artistName
) {
}
