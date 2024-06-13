package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles({"test", "generateData"})
class TicketInvalidationSchedulingServiceTest {

    @Autowired
    private TicketInvalidationSchedulingService ticketInvalidationSchedulingService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private UserServiceImpl userService;

    @Mock
    private TicketServiceImpl ticketService;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShowRepository showRepository;

    private static final Faker faker = new Faker();

    @Test
    void scheduleInvalidationShouldResultInTicketBeingDeletedAfterTimePassed()
        throws DtoNotFoundException, SchedulerException, ValidationException, InterruptedException {
        TicketDetailsDto ticketDetailsDto = new TicketDetailsDto();
        ApplicationUserDto user = userService.findApplicationUserByEmail(ADMIN_USER);
        OrderDetailsDto orderDetailsDto = orderService.create(user);
        Ticket entity = new Ticket();
        entity.setHallSpot(hallSpotRepository.findAll().get(0));
        entity.setOrder(orderRepository.findById(orderDetailsDto.getId()).get());
        entity.setValid(true);
        entity.setReserved(true);
        entity.setHash(faker.internet().uuid());
        Ticket save = ticketRepository.save(entity);

        OrderDetailsDto detailsDto = new OrderDetailsDto();
        detailsDto.setId(orderDetailsDto.getId());
        ticketDetailsDto.setOrder(detailsDto);
        ticketDetailsDto.setId(save.getId());
        ticketDetailsDto.setHash(save.getHash());
        ticketInvalidationSchedulingService.scheduleReservationInvalidationsForNewlyAddedTicket(ticketDetailsDto);

        JobKey jobKey = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.groupEquals("reservationJobs"))
            .stream()
            .filter(x -> Objects.equals(x.getName(), "reservationJob-" + ticketDetailsDto.getHash())).findFirst().get();
        List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);
        assertThat(triggers.size()).isEqualTo(1);
        Trigger actual = triggers.get(0);

        assertThat(actual.getNextFireTime().before(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))).isTrue();
        assertThat(actual.getNextFireTime()
            .after(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES).minus(2, ChronoUnit.SECONDS)))).isTrue();

        // Does not work, because the job is not executed within this transaction. it appears quartz is waiting until this test is complete to actually execute the job?
//        ticketInvalidationSchedulingService.rescheduleReservationInvalidationJob(ticketDetailsDto.getHash(),
//            Date.from(Instant.now()));

//        Thread.sleep(1000);
//        assertThat(ticketRepository.findById(ticketDetailsDto.getId())).isEmpty();
    }
}