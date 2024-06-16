package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import static at.ac.tuwien.sepr.groupphase.backend.service.job.InvalidateReservationJob.RESERVATION_JOB_HASH_VARIABLE;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.job.InvalidateReservationJob;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class TicketInvalidationSchedulingService {

    protected static final int DEFAULT_RESERVATION_PERIOD = 30;

    private final SchedulerFactoryBean schedulerFactoryBean;
    private final OrderDao orderDao;

    public TicketInvalidationSchedulingService(SchedulerFactoryBean schedulerFactoryBean, OrderDao orderDao) {
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.orderDao = orderDao;
    }

    private void scheduleReservationInvalidationJob(String reservationId, Date executionTime) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobDetail jobDetail = JobBuilder.newJob(InvalidateReservationJob.class)
            .withIdentity("reservationJob-" + reservationId, "reservationJobs")
            .usingJobData(RESERVATION_JOB_HASH_VARIABLE, reservationId)
            .storeDurably()
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger-" + reservationId, "reservationTriggers")
            .startAt(executionTime)
            .forJob(jobDetail)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void rescheduleReservationInvalidationJob(String ticketHash, Date newExecutionTime) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        TriggerKey triggerKey = new TriggerKey("trigger-" + ticketHash, "reservationTriggers");
        Trigger newTrigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerKey)
            .startAt(newExecutionTime)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .forJob("reservationJob-" + ticketHash, "reservationJobs")
            .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    public void scheduleReservationInvalidationsForNewlyAddedTicket(TicketDetailsDto ticketDetailsDto)
        throws SchedulerException {
        scheduleReservationInvalidationJob(ticketDetailsDto.getHash(),
            Date.from(Instant.now().plus(DEFAULT_RESERVATION_PERIOD, ChronoUnit.MINUTES)));
        OrderDetailsDto byId = null;
        try {
            byId = orderDao.findById(ticketDetailsDto.getOrder().getId());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Could not find order for ticket", e);
        }
        for (TicketDetailsDto ticket : byId.getTickets()) {
            if (!ticket.getId().equals(ticketDetailsDto.getId())) {
                rescheduleReservationInvalidationJob(ticket.getHash(),
                    Date.from(Instant.now().plus(DEFAULT_RESERVATION_PERIOD, ChronoUnit.MINUTES)));
            }
        }
    }

    public void rescheduleReservationInvalidationJobForConfirmedOrder(TicketDetailsDto ticketDetailsDto)
        throws SchedulerException {
        LocalDateTime rescheduleTime = ticketDetailsDto.getShow().getDateTime()
            .minus(DEFAULT_RESERVATION_PERIOD, ChronoUnit.MINUTES);
        rescheduleReservationInvalidationJob(ticketDetailsDto.getHash(),
            Date.from(rescheduleTime.toInstant(ZoneOffset.UTC)));

    }

    public void cancelReservationInvalidationJob(String hash) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            scheduler.deleteJob(new JobKey("reservationJob-" + hash, "reservationJobs"));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Could not cancel reservation invalidation job", e);
        }
    }
}
