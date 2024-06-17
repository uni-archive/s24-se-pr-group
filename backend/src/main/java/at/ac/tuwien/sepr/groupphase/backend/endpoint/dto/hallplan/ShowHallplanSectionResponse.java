package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import java.util.List;

public record ShowHallplanSectionResponse(
    Long id,
    String name,
    String color,
    boolean standingOnly,
    long price,
    String frontendCoordinates,
    int spotCount,
    int availableSpotCount,
    List<ShowHallplanSpotResponse> spots) {
}
