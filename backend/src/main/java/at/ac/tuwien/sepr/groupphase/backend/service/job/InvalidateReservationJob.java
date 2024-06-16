package at.ac.tuwien.sepr.groupphase.backend.service.job;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class InvalidateReservationJob extends QuartzJobBean {

    public static final String RESERVATION_JOB_HASH_VARIABLE = "ticketHash";
    private final TicketService ticketService;
    private final OrderService orderService;

    public InvalidateReservationJob(TicketService ticketService, OrderService orderService) {
        this.ticketService = ticketService;
        this.orderService = orderService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            TicketDetailsDto ticket = ticketService.findByHash(
                context.getMergedJobDataMap().getString(RESERVATION_JOB_HASH_VARIABLE));

            ticketService.deleteTicket(ticket.getId(), ticket.getOrder().getCustomer());
        } catch (DtoNotFoundException | ValidationException | ForbiddenException e) {
            throw new IllegalStateException(e);
        }
    }
}
