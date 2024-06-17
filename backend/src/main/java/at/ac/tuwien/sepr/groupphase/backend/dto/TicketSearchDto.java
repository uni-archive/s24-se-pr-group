package at.ac.tuwien.sepr.groupphase.backend.dto;

import org.springframework.data.domain.Pageable;

public record TicketSearchDto(Long showId, String firstName, String familyName, boolean reservedOnly, boolean valid, Pageable pageable) {

}
