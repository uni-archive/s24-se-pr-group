package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import org.springframework.data.domain.Pageable;

public record ApplicationUserSearchDto(
    String firstName,
    String familyName,
    String email,
    boolean isLocked,
    Pageable pageable
) {
}
