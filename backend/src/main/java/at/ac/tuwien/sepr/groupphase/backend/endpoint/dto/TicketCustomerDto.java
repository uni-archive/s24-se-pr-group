package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record TicketCustomerDto(
    ApplicationUserResponse applicationUserDto,
    TicketDetailsResponse ticketDetailsDto
) {

}
