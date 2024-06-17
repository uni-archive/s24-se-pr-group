package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import java.sql.Blob;
import java.util.List;

public record ShowHallplanResponse(
    Long id,
    String name,
    String backgroundImage,
    List<ShowHallplanSectionResponse> sectors
) {
}
