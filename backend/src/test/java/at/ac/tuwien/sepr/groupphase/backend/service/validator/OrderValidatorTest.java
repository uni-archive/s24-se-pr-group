package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aCustomerUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class OrderValidatorTest {

    @Autowired
    private OrderValidator orderValidator;

    @Test
    void validateForFindById_if_ValidOrder_ThenDoNothing() throws ValidationException {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        var invoice = new InvoiceDto();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setDateTime(LocalDateTime.now());
        order.setCustomer(user);
        order.setDateTime(LocalDateTime.now());
        order.setInvoices(List.of(invoice));

        orderValidator.validateForFindById(order, user);
    }

    @Test
    void validateForFindById_if_ProvidedUserAndFoundCustomerInOrderDoNotMatch_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        var invoice = new InvoiceDto();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setDateTime(LocalDateTime.now());
        order.setCustomer(user);
        order.setDateTime(LocalDateTime.now());
        order.setInvoices(List.of(invoice));

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
        var invoice = new InvoiceDto();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setDateTime(LocalDateTime.now());
        order.setCustomer(user);
        order.setInvoices(List.of(invoice));
        order.setDateTime(LocalDateTime.now());

        orderValidator.validateForCancel(order, user);
    }

    @Test
    void validateForCancel_if_ProvidedUserAndFoundCustomerInOrderDoNotMatch_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        var invoice = new InvoiceDto();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setDateTime(LocalDateTime.now());
        order.setCustomer(user);
        order.setInvoices(List.of(invoice));
        order.setDateTime(LocalDateTime.now());

        var wrongUser = aCustomerUser().setId(20L);
        assertThatThrownBy(() -> orderValidator.validateForCancel(order, wrongUser))
            .hasMessageContaining("User-ID of order does not match with the given user.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_OrderAlreadyCancelled_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var purchaseInvoice = new InvoiceDto();
        purchaseInvoice.setInvoiceType(InvoiceType.PURCHASE);
        purchaseInvoice.setDateTime(LocalDateTime.now());
        var cancellationInvoice = new InvoiceDto();
        cancellationInvoice.setInvoiceType(InvoiceType.CANCELLATION);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        order.setInvoices(List.of(purchaseInvoice, cancellationInvoice));
        order.setDateTime(LocalDateTime.now());

        assertThatThrownBy(() -> orderValidator.validateForCancel(order, user))
            .hasMessageContaining("Order already cancelled.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_OrderExceededValidationPeriod_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        var exceededInvoice = new InvoiceDto();
        exceededInvoice.setInvoiceType(InvoiceType.PURCHASE);
        exceededInvoice.setDateTime(LocalDateTime.now().minusDays(15));
        order.setCustomer(user);
        order.setInvoices(List.of(exceededInvoice));

        assertThatThrownBy(() -> orderValidator.validateForCancel(order, user))
            .hasMessageContaining("Order exceeded cancellation period.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_ShowInOrderAlreadyStarted_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        var invoice = new InvoiceDto();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setDateTime(LocalDateTime.now());
        order.setInvoices(List.of(invoice));
        order.setCustomer(user);
        var startedShow = new ShowDto();
        startedShow.setDateTime(LocalDateTime.now());
        var ticket = new TicketDetailsDto();
        ticket.setShow(startedShow);
        order.setTickets(List.of(ticket));

        assertThatThrownBy(() -> orderValidator.validateForCancel(order, user))
            .hasMessageContaining("At least one show has already started.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForCancel_if_OrderHasNotBeenPurchasedYet_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);

        assertThatThrownBy(() -> orderValidator.validateForCancel(order, user))
            .hasMessageContaining("Order hasn't been finalized yet.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForPurchase_if_ProvidedUserAndFoundCustomerInOrderDoNotMatch_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);

        var wrongUser = aCustomerUser();
        wrongUser.setId(-1L);

        assertThatThrownBy(() -> orderValidator.validateForPurchase(order, wrongUser))
            .hasMessageContaining("User-ID of order does not match with the given user.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForPurchase_if_OrderHasAlreadyBeenPurchased_ThenThrowValidationException() {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);
        var purchaseInvoice = new InvoiceDto();
        purchaseInvoice.setInvoiceType(InvoiceType.PURCHASE);
        purchaseInvoice.setDateTime(LocalDateTime.now());
        order.setInvoices(List.of(purchaseInvoice));

        assertThatThrownBy(() -> orderValidator.validateForPurchase(order, user))
            .hasMessageContaining("Order is already finalized.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForPurchase_if_PurchaseInformationIsCorrect_ThenDoNothing() throws ValidationException {
        var user = aCustomerUser();
        user.setId(10L);
        var order = new OrderDetailsDto();
        order.setCustomer(user);

        orderValidator.validateForPurchase(order, user);
    }
}
