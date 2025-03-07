package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.InvoiceDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.OrderValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aCustomerUser;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceImplTest {

    private static ApplicationUser testCustomer;
    private static List<Ticket> testTickets;
    private static Ticket reservedTicket;
    private static Ticket ticketToBuy;
    private static List<Invoice> testInvoices;

    private static Order testCancelOrder;
    private static List<Ticket> testCancelTickets;
    private static List<Invoice> testCancelInvoices;

    private static Order testPurchaseOrder;
    private static List<Ticket> testPurchaseTickets;
    private static List<Invoice> testPurchaseInvoices;

    @MockBean
    private OrderValidator orderValidator;
    @Captor
    private ArgumentCaptor<OrderDetailsDto> orderDto;
    @Captor
    private ArgumentCaptor<ApplicationUserDto> applicationUserDto;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private HallSpotRepository hallSpotRepository;
    @Autowired
    private HallSectorRepository hallSectorRepository;
    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;
    @Autowired
    private HallPlanRepository hallPlanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private TicketServiceImpl ticketService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private UserServiceImpl userService;

    private static Faker faker = new Faker();

    @BeforeEach
    void setup() {
        setupCancelOrderTestData();
        setupPurchaseOrderTestData();
    }

    private void setupCancelOrderTestData() {
        var event = new Event();
        var show = new Show();
        show.setEvent(event);
        show.setDateTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        event.setShows(List.of(show));
        eventRepository.save(event);
        showRepository.save(show);

        var hallplan = new HallPlan();
        hallPlanRepository.save(hallplan);

        var sector = new HallSector();
        sector.setHallPlan(hallplan);
        hallSectorRepository.save(sector);

        var hallSpot1 = new HallSpot();
        hallSpot1.setSector(sector);
        hallSpotRepository.save(hallSpot1);

        var hallSpot2 = new HallSpot();
        hallSpot2.setSector(sector);
        hallSpotRepository.save(hallSpot2);

        var hallSpot3 = new HallSpot();
        hallSpot3.setSector(sector);
        hallSpotRepository.save(hallSpot3);

        var hallSpot4 = new HallSpot();
        hallSpot4.setSector(sector);
        hallSpotRepository.save(hallSpot4);

        var hallSpot5 = new HallSpot();
        hallSpot5.setSector(sector);
        hallSpotRepository.save(hallSpot5);

        var hallSpot6 = new HallSpot();
        hallSpot6.setSector(sector);
        hallSpotRepository.save(hallSpot6);

        var sectorShow = new HallSectorShow();
        sectorShow.setSector(sector);
        sectorShow.setShow(show);
        sectorShow.setPrice(50);
        hallSectorShowRepository.save(sectorShow);

        testCustomer = new ApplicationUser();
        testCustomer.setEmail(faker.internet().emailAddress());
        userRepository.save(testCustomer);

        testCancelOrder = new Order();
        testCancelOrder.setCustomer(testCustomer);
        orderRepository.save(testCancelOrder);

        var t1 = new Ticket();
        t1.setHash("hash");
        t1.setReserved(true);
        t1.setValid(true);
        t1.setHallSpot(hallSpot1);
        t1.setShow(show);
        t1.setOrder(testCancelOrder);

        var t2 = new Ticket();
        t2.setHash("hash");
        t2.setReserved(true);
        t2.setValid(false);
        t2.setHallSpot(hallSpot2);
        t2.setShow(show);
        t2.setOrder(testCancelOrder);

        var t3 = new Ticket();
        t3.setHash("hash");
        t3.setReserved(false);
        t3.setValid(true);
        t3.setHallSpot(hallSpot3);
        t3.setShow(show);
        t3.setOrder(testCancelOrder);

        var t4 = new Ticket();
        t4.setHash("hash");
        t4.setReserved(false);
        t4.setValid(false);
        t4.setHallSpot(hallSpot4);
        t4.setShow(show);
        t4.setOrder(testCancelOrder);

        testCancelTickets = List.of(t1, t2, t3, t4);
        var t5 = new Ticket();
        t5.setHash("hash");
        t5.setReserved(false);
        t5.setValid(false);
        t5.setHallSpot(hallSpot5);
        t5.setShow(show);

        var t6 = new Ticket();
        t6.setHash("hash");
        t6.setReserved(true);
        t6.setValid(false);
        t6.setHallSpot(hallSpot6);
        t6.setShow(show);

        testTickets = List.of(t1, t2, t3, t4);
        ticketToBuy = t5;
        reservedTicket = t6;

        ticketRepository.saveAll(testCancelTickets);

        ticketRepository.saveAll(testTickets);
        var invoice1 = new Invoice();
        invoice1.setInvoiceType(InvoiceType.PURCHASE);
        invoice1.setOrder(testCancelOrder);

        testCancelInvoices = List.of(invoice1);

        invoiceRepository.saveAll(testCancelInvoices);
    }

    private void setupPurchaseOrderTestData() {
        var event = new Event();
        var show = new Show();
        show.setDateTime(LocalDateTime.now());
        show.setEvent(event);
        event.setShows(List.of(show));
        eventRepository.save(event);
        showRepository.save(show);

        var hallplan = new HallPlan();
        hallPlanRepository.save(hallplan);

        var sector = new HallSector();
        sector.setHallPlan(hallplan);
        hallSectorRepository.save(sector);

        var hallSpot1 = new HallSpot();
        hallSpot1.setSector(sector);
        hallSpotRepository.save(hallSpot1);

        var hallSpot2 = new HallSpot();
        hallSpot2.setSector(sector);
        hallSpotRepository.save(hallSpot2);

        var hallSpot3 = new HallSpot();
        hallSpot3.setSector(sector);
        hallSpotRepository.save(hallSpot3);

        var hallSpot4 = new HallSpot();
        hallSpot4.setSector(sector);
        hallSpotRepository.save(hallSpot4);

        var sectorShow = new HallSectorShow();
        sectorShow.setSector(sector);
        sectorShow.setShow(show);
        sectorShow.setPrice(100);
        hallSectorShowRepository.save(sectorShow);

        testPurchaseOrder = new Order();
        testPurchaseOrder.setCustomer(testCustomer);
        orderRepository.save(testPurchaseOrder);

        var t1 = new Ticket();
        t1.setHash("hash");
        t1.setReserved(true);
        t1.setValid(false);
        t1.setHallSpot(hallSpot1);
        t1.setShow(show);
        t1.setOrder(testPurchaseOrder);

        var t2 = new Ticket();
        t2.setHash("hash");
        t2.setReserved(true);
        t2.setValid(false);
        t2.setHallSpot(hallSpot2);
        t2.setShow(show);
        t2.setOrder(testPurchaseOrder);

        var t3 = new Ticket();
        t3.setHash("hash");
        t3.setReserved(false);
        t3.setValid(false);
        t3.setHallSpot(hallSpot3);
        t3.setShow(show);
        t3.setOrder(testPurchaseOrder);

        testPurchaseTickets = List.of(t1, t2, t3);

        ticketRepository.saveAll(testPurchaseTickets);
    }

    @AfterEach
    void teardown() {
        invoiceRepository.deleteAll();
        ticketRepository.deleteAll();
        orderRepository.deleteAll();
        hallSectorShowRepository.deleteAll();
        hallSpotRepository.deleteAll();
        hallSectorRepository.deleteAll();
        hallPlanRepository.deleteAll();
        showRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void finding_allOrdersForAUser_ReturnsAListOfAssociatedOrders_WithSummaryDetailsFilledIn() {
        var found = orderService.findForUser(testCustomer.getId());
        assertThat(found)
            .isNotNull()
            .hasSize(2);

        var cancelOrder = found.get(0);
        assertThat(cancelOrder)
            .isNotNull();

        assertThat(cancelOrder.getTotalPriceNonReserved()).isEqualTo(100);
        assertThat(cancelOrder.getTicketCount()).isEqualTo(4);

        var expectedInvoiceIds = testCancelInvoices.stream().map(Invoice::getId).toArray(Long[]::new);
        assertThat(cancelOrder.getInvoices())
            .isNotNull()
            .hasSize(1)
            .map(InvoiceDto::getId)
            .containsExactlyInAnyOrder(expectedInvoiceIds);
    }

    @Test
    void cancelling_anOrder_ShouldCreateANewCancellationInvoice_And_InvalidateAllAssociatedTickets()
        throws ValidationException, EntityNotFoundException, DtoNotFoundException {
        var c = aCustomerUser()
            .setId(testCancelOrder.getCustomer().getId());

        orderService.cancelOrder(testCancelOrder.getId(), c);
        var found = orderService.findById(testCancelOrder.getId(), c);

        assertThat(found.getInvoices())
            .hasSize(2);

        var cancellationInvoice = found.getInvoices().get(1);

        assertThat(cancellationInvoice)
            .isNotNull();

        assertThat(cancellationInvoice.getInvoiceType())
            .isEqualTo(InvoiceType.CANCELLATION);

        assertThat(found.getTickets())
            .allMatch(not(TicketDetailsDto::isValid));
    }

    @Test
    void finding_anOrder_ShouldCallValidator()
        throws ValidationException, DtoNotFoundException {
        orderService.findById(testCancelOrder.getId(), aCustomerUser());
        verify(orderValidator).validateForFindById(orderDto.capture(), applicationUserDto.capture());
    }

    @Test
    void cancelling_anOrder_ShouldCallValidator() throws ValidationException, EntityNotFoundException {
        orderService.cancelOrder(testCancelOrder.getId(), aCustomerUser());
        verify(orderValidator).validateForCancel(orderDto.capture(), applicationUserDto.capture());
    }

    @Test
    void purchasing_anOrder_ShouldCreate_aPurchaseInvoice_And_SetAllAssociatedTicketsAsValid()
        throws ValidationException, DtoNotFoundException {
        var c = aCustomerUser()
            .setId(testPurchaseOrder.getCustomer().getId());

        orderService.purchaseOrder(testPurchaseOrder.getId(), c);

        assertThat(testPurchaseOrder.getInvoices())
            .isNull();
        // TODO: maybe we should change this behaviour to return an empty list instead.

        var found = orderService.findById(testPurchaseOrder.getId(), c);

        assertThat(found.getInvoices())
            .hasSize(1);

        var purchaseInvoice = found.getInvoices().get(0);

        assertThat(purchaseInvoice)
            .isNotNull();

        assertThat(purchaseInvoice.getInvoiceType())
            .isEqualTo(InvoiceType.PURCHASE);

        var reservedTickets = testPurchaseTickets
            .stream()
            .filter(Ticket::isReserved)
            .map(Ticket::getId)
            .toList();

        assertThat(found.getTickets())
            .filteredOn(x -> !reservedTickets.contains(x.getId()))
            .allMatch(TicketDetailsDto::isValid);
    }

    @Test
    void finding_OrderSummaries_For_aUser_Computes_TicketCount_And_TotalPrice_Correctly() throws ValidationException, DtoNotFoundException {
        var orderSummaries = orderService.findForUser(testCustomer.getId());
        assertThat(orderSummaries)
            .isNotNull()
            .hasSize(2);

        var testPurchaseOrderSummary = orderSummaries.stream().filter(o -> o.getId().equals(testPurchaseOrder.getId())).findFirst().get();
        var testCancelOrderSummary = orderSummaries.stream().filter(o -> o.getId().equals(testCancelOrder.getId())).findFirst().get();

        assertAll(
            () -> assertThat(testPurchaseOrderSummary.getTicketCount()).isEqualTo(3),
            () -> assertThat(testPurchaseOrderSummary.getTotalPriceNonReserved()).isEqualTo(100),
            () -> assertThat(testPurchaseOrderSummary.getTotalPriceReserved()).isEqualTo(200),

            () -> assertThat(testCancelOrderSummary.getTicketCount()).isEqualTo(4),
            () -> assertThat(testCancelOrderSummary.getTotalPriceNonReserved()).isEqualTo(2 * 50),
            () -> assertThat(testCancelOrderSummary.getTotalPriceReserved()).isEqualTo(2 * 50)
        );

        var c = new ApplicationUserDto();
        c.setId(testCustomer.getId());
        var purchaseOrderDetails = orderService.findById(testPurchaseOrder.getId(), c);

        assertThat(testPurchaseOrderSummary.getTotalPriceReserved())
            .isEqualTo(purchaseOrderDetails
                .getTickets()
                .stream()
                .filter(TicketDetailsDto::isReserved)
                .mapToLong(t -> t.getHallSpot().getSector().getHallSectorShow().getPrice())
                .sum()
            );

        assertThat(testPurchaseOrderSummary.getTotalPriceNonReserved())
            .isEqualTo(purchaseOrderDetails
                .getTickets()
                .stream()
                .filter(not(TicketDetailsDto::isReserved))
                .mapToLong(t -> t.getHallSpot().getSector().getHallSectorShow().getPrice())
                .sum()
            );

        var cancelOrderDetails = orderService.findById(testCancelOrder.getId(), c);

        assertThat(testCancelOrderSummary.getTotalPriceReserved())
            .isEqualTo(cancelOrderDetails
                .getTickets()
                .stream()
                .filter(TicketDetailsDto::isReserved)
                .mapToLong(t -> t.getHallSpot().getSector().getHallSectorShow().getPrice())
                .sum()
            );

        assertThat(testCancelOrderSummary.getTotalPriceNonReserved())
            .isEqualTo(cancelOrderDetails
                .getTickets()
                .stream()
                .filter(not(TicketDetailsDto::isReserved))
                .mapToLong(t -> t.getHallSpot().getSector().getHallSectorShow().getPrice())
                .sum()
            );
    }

}
