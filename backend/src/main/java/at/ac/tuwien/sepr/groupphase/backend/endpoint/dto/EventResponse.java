package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;

public record EventResponse(
    Long id,
    String title,
    String description,
    EventType eventType, // TODO: Where to store this type?
    Long duration
) {
}
