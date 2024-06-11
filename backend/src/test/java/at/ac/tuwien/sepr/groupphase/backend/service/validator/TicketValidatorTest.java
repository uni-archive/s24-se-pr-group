package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class TicketValidatorTest {

    @Autowired
    private TicketValidator ticketValidator;

    private static Order testOrderNonPurchased;
    private static Order testOrderPurchased;
    private static Order testOrderCancelled;
    private static ApplicationUser user;

    private Show testShow;
    private HallSpot testHallSpot;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private HallSectorRepository hallSectorRepository;

    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        user = new ApplicationUser();
        userRepository.save(user);

        testShow = new Show();
        testShow.setDateTime(LocalDateTime.now().plusDays(1));
        showRepository.save(testShow);

        var sec = new HallSector();
        testHallSpot = new HallSpot();
        testHallSpot.setSector(sec);

        hallSectorRepository.save(sec);
        hallSpotRepository.save(testHallSpot);

        var secShow = new HallSectorShow();
        secShow.setShow(testShow);
        secShow.setSector(sec);
        hallSectorShowRepository.save(secShow);


        testOrderNonPurchased = new Order();
        testOrderNonPurchased.setCustomer(user);

        testOrderPurchased = new Order();
        var i1 = new Invoice();
        i1.setInvoiceType(InvoiceType.PURCHASE);
        i1.setOrder(testOrderPurchased);
        testOrderPurchased.setInvoices(List.of(i1));
        testOrderPurchased.setCustomer(user);

        testOrderCancelled = new Order();
        var i2 = new Invoice();
        i2.setInvoiceType(InvoiceType.CANCELLATION);
        i2.setOrder(testOrderCancelled);
        testOrderCancelled.setInvoices(List.of(i2));
        testOrderCancelled.setCustomer(user);

        orderRepository.saveAll(List.of(testOrderNonPurchased, testOrderPurchased, testOrderCancelled));
        invoiceRepository.saveAll(List.of(i1, i2));
    }

    @AfterEach
    void teardown() {
        ticketRepository.deleteAll();
        invoiceRepository.deleteAll();
        orderRepository.deleteAll();
        hallSectorShowRepository.deleteAll();
        showRepository.deleteAll();
        hallSpotRepository.deleteAll();
        hallSectorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void aNonReservedTicket_ShouldNotBeCancellable() {
        var t = new TicketDetailsDto();
        t.setReserved(false);
        t.setValid(true);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel non-reserved ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void aReservedTicket_ThatIsInvalid_ShouldNotBeCancellable() {
        var t = new TicketDetailsDto();
        t.setReserved(true);
        t.setValid(false);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel invalid ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void ticketsShouldFirstBeCheckedForValidity() {
        var t = new TicketDetailsDto();
        t.setReserved(false);
        t.setValid(false);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel invalid ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void adding_aTicketToAn_NonPurchasedOrder_DoesNothing() throws ValidationException {
        ticketValidator.validateForCreate(new TicketAddToOrderDto(
            testHallSpot.getId(),
            testOrderNonPurchased.getId(),
            testShow.getId(),
            true
        ));
    }

    @Test
    void adding_aTicketToA_PurchasedOrder_ThrowsValidationException() {
        assertThatThrownBy(() -> ticketValidator.validateForCreate(new TicketAddToOrderDto(
            testHallSpot.getId(),
            testOrderPurchased.getId(),
            testShow.getId(),
            true
        )))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void adding_aTicketToA_CancelledOrder_ThrowsValidationException() {
        assertThatThrownBy(() -> ticketValidator.validateForCreate(new TicketAddToOrderDto(
            testHallSpot.getId(),
            testOrderCancelled.getId(),
            testShow.getId(),
            true
        )))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void removing_aTicketFromAn_NonPurchasedOrder_DoesNothing() throws ValidationException {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);
        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));

        ticketValidator.validateForDelete(ticket.getId(), order);
    }

    @Test
    void removing_aTicket_fromA_PurchasedOrder_ThrowsValidationException() {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);

        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));
        order.setInvoices(List.of(
            new InvoiceDto.InvoiceDtoBuilder()
                .withInvoiceType(InvoiceType.PURCHASE)
                .build()
        ));
        assertThatThrownBy(() -> ticketValidator.validateForDelete(ticket.getId(), order))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void removing_aTicket_fromA_CancelledOrder_ThrowsValidationException() {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);

        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));
        order.setInvoices(List.of(
            new InvoiceDto.InvoiceDtoBuilder()
                .withInvoiceType(InvoiceType.CANCELLATION)
                .build()
        ));
        assertThatThrownBy(() -> ticketValidator.validateForDelete(ticket.getId(), order))
            .isInstanceOf(ValidationException.class);
    }


    @Test
    void changing_theReservedStateOf_aTicket_fromAn_NonPurchasedOrder_DoesNothing() throws ValidationException {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);
        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));

        ticketValidator.validateForChangeTicketReserved(ticket.getId(), order);
    }

    @Test
    void changing_theReservedStateOf_aTicketFromA_PurchasedOrder_ThrowsValidationException() {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);

        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));
        order.setInvoices(List.of(
            new InvoiceDto.InvoiceDtoBuilder()
                .withInvoiceType(InvoiceType.PURCHASE)
                .build()
        ));
        assertThatThrownBy(() -> ticketValidator.validateForChangeTicketReserved(ticket.getId(), order))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void changing_theReservedStateOf_aTicket_fromA_CancelledOrder_ThrowsValidationException() {
        var ticket = new TicketDetailsDto();
        ticket.setId(-10L);

        var order = new OrderDetailsDto();
        order.setTickets(List.of(ticket));
        order.setInvoices(List.of(
            new InvoiceDto.InvoiceDtoBuilder()
                .withInvoiceType(InvoiceType.CANCELLATION)
                .build()
        ));
        assertThatThrownBy(() -> ticketValidator.validateForChangeTicketReserved(ticket.getId(), order))
            .isInstanceOf(ValidationException.class);
    }


    @Test
    void validateForValidation_ShouldThrowException_WhenTicketIsNull() {
        assertThatThrownBy(() -> ticketValidator.validateForValidation(null))
            .hasMessageContaining("Ticket is null")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForValidation_ShouldThrowException_WhenTicketIsValid() {
        var t = new TicketDetailsDto();
        t.setReserved(true);
        t.setValid(true);

        assertThatThrownBy(() -> ticketValidator.validateForValidation(t))
            .hasMessageContaining("Cannot validate valid ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateForValidation_ShouldThrowException_WhenTicketIsNotReserved() {
        var t = new TicketDetailsDto();
        t.setReserved(false);
        t.setValid(false);

        assertThatThrownBy(() -> ticketValidator.validateForValidation(t))
            .hasMessageContaining("Cannot validate not-reserved ticket.")
            .isInstanceOf(ValidationException.class);
    }
}
