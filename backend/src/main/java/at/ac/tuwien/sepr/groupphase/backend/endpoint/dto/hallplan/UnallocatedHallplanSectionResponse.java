package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import java.util.List;

public record UnallocatedHallplanSectionResponse(
    Long id,
    String name,
    String color,
    boolean standingOnly,
    int price,
    String frontendCoordinates,
    List<UnallocatedHallplanSpotResponse> spots) {
}
