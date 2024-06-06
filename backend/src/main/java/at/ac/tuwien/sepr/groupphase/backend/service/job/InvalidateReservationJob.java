package at.ac.tuwien.sepr.groupphase.backend.service.job;

import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class InvalidateReservationJob extends QuartzJobBean {

    private final TicketService ticketService;

    public InvalidateReservationJob(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            ticketService.cancelReservedTicket(context.getMergedJobDataMap().getLong("ticketId"));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        } catch (DtoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
