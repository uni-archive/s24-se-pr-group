package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aCustomerUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class OrderValidatorTest {

    @Autowired
    private OrderValidator orderValidator;

    @Test
    void validateForFindById_if_ProvidedUserAndFoundCustomerInOrderMatch_ThenDoNothing() throws ValidationException {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        orderValidator.validateForFindById(order, user);
    }

    @Test
    void validateForFindById_if_ProvidedUserAndFoundCustomerInOrderDoNotMatch_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);

        var user2 = aCustomerUser();
        user2.setId(20L);
        assertThatThrownBy(() -> orderValidator.validateForFindById(order, user2))
            .hasMessageContaining("User-ID of order does not match with the given user.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_ProvidedUserAndFoundCustomerInOrderMatch_AndOrderIsNotAlreadyCancelled_ThenDoNothing() throws ValidationException {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        order.setInvoices(List.of());
        orderValidator.validateForCancel(order, user);
    }


    @Test
    void validateForCancel_if_ProvidedUserAndFoundCustomerInOrderDoNotMatch_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        order.setInvoices(List.of());
        var wrongUser = aCustomerUser().setId(20L);
        assertThatThrownBy(() -> orderValidator.validateForCancel(order, wrongUser))
            .hasMessageContaining("User-ID of order does not match with the given user.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_OrderAlreadyCancelled_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var cancellationInvoice = new InvoiceDto();
        cancellationInvoice.setInvoiceType(InvoiceType.CANCELLATION);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        order.setInvoices(List.of(cancellationInvoice));

        assertThatThrownBy(() -> orderValidator.validateForCancel(order, user))
            .hasMessageContaining("Order already cancelled.")
            .isInstanceOf(ValidationException.class);
    }
}
