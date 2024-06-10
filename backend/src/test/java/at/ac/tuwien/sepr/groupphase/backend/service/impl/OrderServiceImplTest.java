package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aCustomerUser;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceImplTest {

    private static ApplicationUser testCustomer;
    private static Order testOrder;
    private static List<Ticket> testTickets;
    private static Ticket reservedTicket;
    private static Ticket ticketToBuy;
    private static List<Invoice> testInvoices;

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

        testOrder = new Order();
        testOrder.setCustomer(testCustomer);
        orderRepository.save(testOrder);

        var t1 = new Ticket();
        t1.setHash("hash");
        t1.setReserved(true);
        t1.setValid(true);
        t1.setHallSpot(hallSpot1);
        t1.setShow(show);
        t1.setOrder(testOrder);

        var t2 = new Ticket();
        t2.setHash("hash");
        t2.setReserved(true);
        t2.setValid(false);
        t2.setHallSpot(hallSpot2);
        t2.setShow(show);
        t2.setOrder(testOrder);

        var t3 = new Ticket();
        t3.setHash("hash");
        t3.setReserved(false);
        t3.setValid(true);
        t3.setHallSpot(hallSpot3);
        t3.setShow(show);
        t3.setOrder(testOrder);

        var t4 = new Ticket();
        t4.setHash("hash");
        t4.setReserved(false);
        t4.setValid(false);
        t4.setHallSpot(hallSpot4);
        t4.setShow(show);
        t4.setOrder(testOrder);

        var t5 = new Ticket();
        t5.setHash("hash");
        t5.setReserved(false);
        t5.setValid(false);
        t5.setHallSpot(hallSpot5);
        t5.setShow(show);
        t5.setOrder(testOrder);

        var t6 = new Ticket();
        t6.setHash("hash");
        t6.setReserved(true);
        t6.setValid(false);
        t6.setHallSpot(hallSpot6);
        t6.setShow(show);
        t6.setOrder(testOrder);

        testTickets = List.of(t1, t2, t3, t4);
        ticketToBuy = t5;
        reservedTicket = t6;

        ticketRepository.saveAll(testTickets);
        var invoice1 = new Invoice();
        invoice1.setInvoiceType(InvoiceType.PURCHASE);
        invoice1.setOrder(testOrder);

        testInvoices = List.of(invoice1);

        invoiceRepository.saveAll(testInvoices);
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
            .hasSize(1);

        var order = found.get(0);
        assertThat(order)
            .isNotNull();

        assertThat(order.getTotalPrice()).isEqualTo(200);
        assertThat(order.getTicketCount()).isEqualTo(4);

        var expectedInvoiceIds = testInvoices.stream().map(Invoice::getId).toArray(Long[]::new);
        assertThat(order.getInvoices())
            .isNotNull()
            .hasSize(1)
            .map(InvoiceDto::getId)
            .containsExactlyInAnyOrder(expectedInvoiceIds);
    }

    @Test
    void cancelling_anOrder_ShouldCreateANewCancellationInvoice_And_InvalidateAllAssociatedTickets()
        throws ValidationException, EntityNotFoundException, DtoNotFoundException {
        var c = aCustomerUser()
            .setId(testOrder.getCustomer().getId());

        orderService.cancelOrder(testOrder.getId(), c);
        var found = orderService.findById(testOrder.getId(), c);

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
        throws ValidationException, EntityNotFoundException, DtoNotFoundException {
        orderService.findById(testOrder.getId(), aCustomerUser());
        verify(orderValidator).validateForFindById(orderDto.capture(), applicationUserDto.capture());
    }

    @Test
    void cancelling_anOrder_ShouldCallValidator() throws ValidationException, EntityNotFoundException {
        orderService.cancelOrder(testOrder.getId(), aCustomerUser());
        verify(orderValidator).validateForCancel(orderDto.capture(), applicationUserDto.capture());
    }

    @Test
    void confirmOrderShouldScheduleJobsTo30MinutesBeforeTheShow()
        throws DtoNotFoundException, ValidationException, SchedulerException {
        // given
        ApplicationUserDto user = userService.findApplicationUserByEmail(testCustomer.getEmail());
        OrderDetailsDto orderDetailsDto = orderService.create(user);

        TicketDetailsDto ticketDtoToBuy = ticketService.addTicketToOrder(ticketToBuy.getHallSpot().getId(),
            ticketToBuy.getShow().getId(),
            orderDetailsDto.getId(), false);
        TicketDetailsDto reservedTicketDto = ticketService.addTicketToOrder(reservedTicket.getHallSpot().getId(),
            reservedTicket.getShow().getId(),
            orderDetailsDto.getId(), true);

        orderDetailsDto = orderService.findById(orderDetailsDto.getId(), user);

        // when
        orderService.confirmOrder(orderDetailsDto);

        // then
        assertThat(schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals("reservationJobs"))
            .stream()
            .filter(x -> Objects.equals(x.getName(), "reservationJob-" + ticketDtoToBuy.getHash())).findFirst()).isEmpty();
        JobKey jobKey = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.anyJobGroup()).stream()
            .filter(x -> Objects.equals(x.getName(), "reservationJob-" + reservedTicketDto.getHash())).findFirst().get();
        List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);
        assertThat(triggers.size()).isEqualTo(1);
        Trigger actual = triggers.get(0);

        assertThat(actual.getNextFireTime().before(Date.from(reservedTicket.getShow().getDateTime().minus(30, ChronoUnit.MINUTES).plus(2, ChronoUnit.SECONDS).toInstant(
            ZoneOffset.UTC)))).isTrue();
        assertThat(actual.getNextFireTime()
            .after(Date.from(reservedTicket.getShow().getDateTime().minusMinutes(30).minusSeconds(2).toInstant(ZoneOffset.UTC)))).isTrue();
    }
}
