package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderValidator extends AbstractValidator<OrderDetailsDto> {

    private static final TemporalAmount ORDER_CANCELLATION_PERIOD = Duration.ofDays(14);

    public void validateForFindById(OrderDetailsDto foundOrder, ApplicationUserDto user) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (!foundOrder.getCustomer().getId().equals(user.getId())) {
            errors.add("User-ID of order does not match with the given user.");
        }

        endValidation(errors);
    }

    public void validateForCancel(OrderDetailsDto order, ApplicationUserDto user) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (!order.getCustomer().getId().equals(user.getId())) {
            errors.add("User-ID of order does not match with the given user.");
        }

        if (order.getCancellationInvoice().isPresent()) {
            errors.add("Order already cancelled.");
        }

        var now = LocalDateTime.now();
        if (order.getPurchaseInvoice().getDateTime().plus(ORDER_CANCELLATION_PERIOD).isBefore(now)) {
            errors.add("Order exceeded cancellation period.");
        }

        var showDatesOfOrder = order.getTickets().stream()
            .map(TicketDetailsDto::getShow)
            .map(ShowDto::getDateTime);

        if (showDatesOfOrder.anyMatch(s -> s.isBefore(now))) {
            errors.add("At least one show has already started.");
        }

        endValidation(errors);
    }
}
