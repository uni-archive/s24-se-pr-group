package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import java.sql.Blob;
import java.util.List;

public record UnallocatedHallplanResponse(
    Long id,
    String name,
    Blob backgroundImage,
    List<UnallocatedHallplanSectionResponse> sections
) {
}
