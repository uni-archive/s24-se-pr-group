package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

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

        if (order.getInvoices().stream().anyMatch(i -> i.getInvoiceType().equals(InvoiceType.CANCELLATION))) {
            errors.add("Order already cancelled.");
        }

        var now = LocalDateTime.now();
        if (order.getDateTime().plus(ORDER_CANCELLATION_PERIOD).isBefore(now)) {
            errors.add("Order exceeded cancellation period.");
        }

        endValidation(errors);
    }

    @Override
    public void validateForCreate(OrderDetailsDto object) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(object.getCustomer())) {
            errors.add("Customer must not be null");
        }
        if (Objects.isNull(object.getDateTime())) {
            errors.add("DateTime must not be null");
        }
        if (Objects.nonNull(object.getDateTime()) && object.getDateTime().isAfter(LocalDateTime.now())) {
            errors.add("Order date must not be in the future.");
        }
        endValidation(errors);
    }
}
