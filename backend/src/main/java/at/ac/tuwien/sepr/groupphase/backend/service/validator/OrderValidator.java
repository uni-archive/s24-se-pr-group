package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderValidator extends AbstractValidator<OrderDetailsDto> {

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

        endValidation(errors);
    }
}
