package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public record ShowResponse(
    Long id,
    LocalDateTime dateTime,
    EventResponse event,
    LocationResponse location
) {
}
