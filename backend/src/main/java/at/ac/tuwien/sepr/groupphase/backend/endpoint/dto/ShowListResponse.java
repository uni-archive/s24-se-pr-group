package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShowListResponse(
    long id,
    LocalDateTime dateTime,
    List<ArtistSearchResponse> artistList,
    long eventid,
    long startingPrice,
    String eventName
) {
}
