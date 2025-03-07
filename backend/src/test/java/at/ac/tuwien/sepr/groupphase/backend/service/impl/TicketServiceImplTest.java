package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aCustomerUser;
import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.TicketValidator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles({"test"})
public class TicketServiceImplTest implements TestData {

    @MockBean
    private TicketValidator ticketValidator;

    @Captor
    private ArgumentCaptor<TicketDetailsDto> ticketDto;

    @Captor
    private ArgumentCaptor<OrderDetailsDto> orderDto;

    private long orderId;

    private ApplicationUserDto testUser;

    @Autowired
    private TicketServiceImpl ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private LocationRepository locationRepository;

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
    private OrderServiceImpl orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;


    private static Ticket testTicketValidReserved;
    private static Ticket testTicketInvalidReserved;

    private static Ticket testTicketValidNonReserved;
    private static Ticket testTicketInvalidNonReserved;
    private static HallSpot hallSpot;
    private static Show show;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        var event = new Event();
        show = new Show();
        show.setDateTime(LocalDateTime.of(2025, 1, 1, 12, 0));
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

        var hallSpot5 = new HallSpot();
        hallSpot4.setSector(sector);
        hallSpot = hallSpotRepository.save(hallSpot5);

        var sectorShow = new HallSectorShow();
        sectorShow.setSector(sector);
        sectorShow.setShow(show);
        sectorShow.setPrice(50);
        hallSectorShowRepository.save(sectorShow);

        var user = aUserEntity();
        user.setEmail("foobar@test.com");
        userRepository.save(user);

        testUser = new ApplicationUserDto();
        testUser.setId(user.getId());

        var order = new Order();
        order.setCustomer(user);
        orderRepository.save(order);
        orderId = order.getId();

        testTicketValidReserved = new Ticket();
        testTicketValidReserved.setHash("hash");
        testTicketValidReserved.setReserved(true);
        testTicketValidReserved.setValid(true);
        testTicketValidReserved.setHallSpot(hallSpot1);
        testTicketValidReserved.setShow(show);
        testTicketValidReserved.setOrder(order);
        ticketRepository.save(testTicketValidReserved);

        testTicketInvalidReserved = new Ticket();
        testTicketInvalidReserved.setHash("hash");
        testTicketInvalidReserved.setReserved(true);
        testTicketInvalidReserved.setValid(false);
        testTicketInvalidReserved.setHallSpot(hallSpot2);
        testTicketInvalidReserved.setShow(show);
        testTicketInvalidReserved.setOrder(order);
        ticketRepository.save(testTicketInvalidReserved);

        testTicketValidNonReserved = new Ticket();
        testTicketValidNonReserved.setHash("hash");
        testTicketValidNonReserved.setReserved(false);
        testTicketValidNonReserved.setValid(true);
        testTicketValidNonReserved.setHallSpot(hallSpot3);
        testTicketValidNonReserved.setShow(show);
        testTicketValidNonReserved.setOrder(order);
        ticketRepository.save(testTicketValidNonReserved);

        testTicketInvalidNonReserved = new Ticket();
        testTicketInvalidNonReserved.setHash("hash");
        testTicketInvalidNonReserved.setReserved(false);
        testTicketInvalidNonReserved.setValid(false);
        testTicketInvalidNonReserved.setHallSpot(hallSpot4);
        testTicketInvalidNonReserved.setShow(show);
        testTicketInvalidNonReserved.setOrder(order);
        ticketRepository.save(testTicketInvalidNonReserved);

        if (userRepository.findByEmail(ADMIN_USER) == null) {
            createUser();
        }
    }

    private void createUser() {
        var user = new ApplicationUser();
        user.setEmail(ADMIN_USER);
        user.setPassword(passwordEncoder.encode("password"));
        user.setType(UserType.ADMIN);
        userRepository.save(user);

        var user2 = new ApplicationUser();
        user2.setEmail(DEFAULT_USER);
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setType(UserType.CUSTOMER);
        userRepository.save(user2);
    }

    @AfterEach
    void teardown() {
        ticketRepository.deleteAll();
        hallSectorShowRepository.deleteAll();
        showRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        hallSpotRepository.deleteAll();
        hallSectorRepository.deleteAll();
        hallPlanRepository.deleteAll();
        orderRepository.deleteById(orderId);
        userRepository.deleteById(testUser.getId());
    }

    @Test
    void fetching_aTicket_ShouldPopulateItsAssignedSpotWith_aSectorShow() throws DtoNotFoundException {
        var found = ticketService.findById(testTicketValidReserved.getId());
        assertThat(found.getHallSpot()).isNotNull();
        assertThat(found.getHallSpot().getSector()).isNotNull();
        assertThat(found.getHallSpot().getSector().getHallSectorShow()).isNotNull();
        assertThat(found.getHallSpot().getSector().getHallSectorShow().getPrice()).isEqualTo(50);
    }

    @Test
    void cancelling_aReservation_ShouldDeleteTheTicket() throws ValidationException, DtoNotFoundException {
        ticketService.cancelReservedTicket(testTicketValidReserved.getId());
        assertThat(ticketRepository.findById(testTicketValidReserved.getId())).isEmpty();
    }

    @Test
    void cancelling_aNonExistentTicket_ThrowsNotFoundException() {
        assertThatThrownBy(() -> ticketService.cancelReservedTicket(-1L))
            .isInstanceOf(DtoNotFoundException.class);
    }

    @Test
    void cancelling_aReservation_ShouldCallValidator() throws ValidationException, DtoNotFoundException {
        ticketService.cancelReservedTicket(testTicketValidReserved.getId());
        verify(ticketValidator).validateForCancelReservation(ticketDto.capture());
    }

    @Test
    void creating_aTicket_ShouldAddToOrderAndScheduleTask()
        throws DtoNotFoundException, ValidationException, SchedulerException, ForbiddenException {
        ApplicationUserDto user = userService.findApplicationUserByEmail(ADMIN_USER);
        List<Ticket> previousTickets = ticketRepository.findTicketsByUserId(user.getId());
        int ticketCount = previousTickets.size();
        OrderDetailsDto orderDetailsDto = orderService.create(user);

        TicketDetailsDto ticketDetailsDto = ticketService.addTicketToOrder(new TicketAddToOrderDto(
            hallSpot.getId(),
            orderDetailsDto.getId(),
            show.getId(),
            false
        ), user);

        List<Ticket> newTickets = ticketRepository.findTicketsByUserId(user.getId());
        assertThat(newTickets.size()).isEqualTo(ticketCount + 1);
        Ticket ticket = newTickets.stream().filter(x -> !previousTickets.contains(x)).findFirst().get();
        Assertions.assertAll(
            () -> Assertions.assertEquals(ticketDetailsDto.getHallSpot().getId(), hallSpot.getId()),
            () -> Assertions.assertEquals(ticketDetailsDto.getShow().getId(), show.getId()),
            () -> Assertions.assertEquals(ticketDetailsDto.getOrder().getId(), orderDetailsDto.getId()),
            () -> Assertions.assertNotNull(ticketDetailsDto.getHash()),
            () -> Assertions.assertEquals(ticketDetailsDto.getHash(), ticket.getHash())
        );
        JobKey jobKey = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.anyJobGroup()).stream()
            .filter(x -> Objects.equals(x.getName(), "reservationJob-" + ticket.getHash())).findFirst().get();
        List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);
        assertThat(triggers.size()).isEqualTo(1);
        Trigger actual = triggers.get(0);

        assertThat(actual.getNextFireTime().before(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))).isTrue();
        assertThat(actual.getNextFireTime().after(Date.from(Instant.now().plus(29, ChronoUnit.MINUTES)))).isTrue();
    }

    @Test
    void creating_aTicketOnOrderWithOtherTickets_ShouldRefreshJobsOnAllTickets()
        throws DtoNotFoundException, ValidationException, InterruptedException, SchedulerException, ForbiddenException {
        ApplicationUserDto user = userService.findApplicationUserByEmail(ADMIN_USER);
        OrderDetailsDto orderDetailsDto = orderService.create(user);

        TicketDetailsDto ticketDetailsDto1 = ticketService.addTicketToOrder(new TicketAddToOrderDto(
            hallSpot.getId(),
            orderDetailsDto.getId(),
            show.getId(),
            false
        ), user);

        Thread.sleep(2000);
        TicketDetailsDto ticketDetailsDto2 = ticketService.addTicketToOrder(new TicketAddToOrderDto(
            hallSpot.getId(),
            orderDetailsDto.getId(),
            show.getId(),
            false
        ), user);

        JobKey jobKey = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.anyJobGroup()).stream()
            .filter(x -> Objects.equals(x.getName(), "reservationJob-" + ticketDetailsDto1.getHash())).findFirst().get();
        List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);
        assertThat(triggers.size()).isEqualTo(1);
        Trigger actual = triggers.get(0);

        assertThat(actual.getNextFireTime().before(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))).isTrue();
        assertThat(actual.getNextFireTime()
            .after(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES).minus(2, ChronoUnit.SECONDS)))).isTrue();
    }

    @Test
    void removing_aTicket_ShouldCallValidator() throws ValidationException, ForbiddenException {
        ticketService.deleteTicket(testTicketValidNonReserved.getId(), testUser);
        verify(ticketValidator).validateForDelete(eq(testTicketValidNonReserved.getId()), orderDto.capture());
    }

    @Test
    void changing_theReservedStateOf_aTicket_ShouldCallValidator() throws ValidationException, ForbiddenException, DtoNotFoundException {
        ticketService.changeTicketReserved(testTicketValidNonReserved.getId(), false, testUser);
        verify(ticketValidator).validateForChangeTicketReserved(eq(testTicketValidNonReserved.getId()), orderDto.capture());
    }

    @Test
    void adding_aTicket_ShouldCallValidator() throws ValidationException, ForbiddenException {
        var addTicket = new TicketAddToOrderDto(
            hallSpot.getId(),
            orderId,
            show.getId(),
            false
        );
        ticketService.addTicketToOrder(addTicket, testUser);
        verify(ticketValidator).validateForCreate(eq(addTicket));
    }

    @Test
    void adding_aTicket_WithWrongUser_ThrowsForbiddenException() {
        var addTicket = new TicketAddToOrderDto(
            hallSpot.getId(),
            orderId,
            show.getId(),
            false
        );

        var wrongUser = aCustomerUser();
        wrongUser.setId(-10L);

        assertThatThrownBy(() -> ticketService.addTicketToOrder(addTicket, wrongUser))
            .isInstanceOf(ForbiddenException.class);
    }


    @Test
    void removing_aTicket_WithWrongUser_ThrowsForbiddenException() {
        var wrongUser = aCustomerUser();
        wrongUser.setId(-10L);

        assertThatThrownBy(() -> ticketService.deleteTicket(testTicketValidNonReserved.getId(), wrongUser))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void changing_setReserved_of_aTicket_persistsThatChangeInTheDb() throws DtoNotFoundException, ValidationException, ForbiddenException {
        var ticketId = testTicketValidNonReserved.getId();
        var reservedState = testTicketValidNonReserved.isReserved();
        assertThat(ticketService.findById(ticketId).isReserved())
            .isEqualTo(reservedState);

        ticketService.changeTicketReserved(ticketId, !reservedState, testUser);

        assertThat(ticketService.findById(ticketId).isReserved())
            .isEqualTo(!reservedState);

        ticketService.changeTicketReserved(ticketId, reservedState, testUser);

        assertThat(ticketService.findById(ticketId).isReserved())
            .isEqualTo(reservedState);
    }

    @Test
    void changing_setReserved_of_aTicket_withTheWrongUser_ThrowsForbiddenException() {
        var wrongUser = aCustomerUser();
        wrongUser.setId(-10L);
        var ticketId = testTicketValidNonReserved.getId();

        assertThatThrownBy(() -> ticketService.changeTicketReserved(ticketId, false, wrongUser))
            .isInstanceOf(ForbiddenException.class);
    }
}
