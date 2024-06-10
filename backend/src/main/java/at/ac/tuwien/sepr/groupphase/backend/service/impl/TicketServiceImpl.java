package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.OrderSummaryDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSectorDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSpotDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.HashUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.HallSectorShowService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.TicketValidator;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketDao ticketDao;
    private final HallSpotDao hallSpotDao;
    private final ShowDao showDao;

    private final TicketValidator ticketValidator;

    private final HallSectorShowService hallSectorShowService;
    private final OrderDao orderDao;
    private final TicketInvalidationSchedulingService ticketInvalidationSchedulingService;
    private final HallSectorDao hallSectorDao;

    public TicketServiceImpl(TicketDao ticketDao, HallSpotDao hallSpotDao, ShowDao showDao,
                             TicketValidator ticketValidator, HallSectorShowService hallSectorShowService, OrderDao orderDao,
                             TicketInvalidationSchedulingService ticketInvalidationSchedulingService, HallSectorDao hallSectorDao) {
        this.ticketDao = ticketDao;
        this.hallSpotDao = hallSpotDao;
        this.showDao = showDao;
        this.ticketValidator = ticketValidator;
        this.hallSectorShowService = hallSectorShowService;
        this.orderDao = orderDao;
        this.ticketInvalidationSchedulingService = ticketInvalidationSchedulingService;
        this.hallSectorDao = hallSectorDao;
    }

    @Override
    public TicketDetailsDto findById(long id) throws DtoNotFoundException {
        try {
            var ticket = ticketDao.findById(id);
            loadSectorShowForTicket(ticket);
            return ticket;
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public List<TicketDetailsDto> findForUserById(long userId) {
        var tickets = ticketDao.findByUserId(userId);
        tickets.forEach(this::loadSectorShowForTicket);
        return tickets;
    }

    public void loadSectorShowForTicket(TicketDetailsDto ticket) {
        try {
            // handle cyclic dependencies between sectors and shows that apply to each ticket
            var sectorShow = hallSectorShowService.findByShowIdAndHallSectorId(
                ticket.getShow().getId(),
                ticket.getHallSpot().getSector().getId()
            );
            ticket.getHallSpot().getSector().setHallSectorShow(sectorShow);
        } catch (EntityNotFoundException ex) {
            // invalid state
            throw new IllegalStateException("A ticket must be assigned to a hall-sector-show.");
        }
    }

    @Override
    public void cancelReservedTicket(long id) throws ValidationException, DtoNotFoundException {
        try {
            var ticket = ticketDao.findById(id);
            ticketValidator.validateForCancelReservation(ticket);
            ticketDao.cancelReservedTicket(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public void invalidateAllTicketsForOrder(long orderId) {
        ticketDao.invalidateAllTicketsForOrder(orderId);
    }

    @Override
    public TicketDetailsDto addTicketToOrder(Long seatId, Long showId, Long orderId, boolean reservationOnly)
        throws ValidationException {
        TicketDetailsDto ticketDetailsDto = createTicket(seatId, showId, orderId, reservationOnly);
        try {
            ticketInvalidationSchedulingService.scheduleReservationInvalidationsForNewlyAddedTicket(ticketDetailsDto);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Could not schedule reservation invalidation job", e);
        }
        return ticketDetailsDto;
    }


    private TicketDetailsDto createTicket(Long seatId, Long showId, Long orderId, boolean reservationOnly)
        throws ValidationException {
        TicketDetailsDto ticket = new TicketDetailsDto();
        ticketValidator.validateForCreate(showId, seatId, orderId);
        try {
            ticket.setHallSpot(hallSpotDao.findById(seatId));
            ticket.setShow(showDao.findById(showId));
            ticket.setReserved(reservationOnly);
            ticket.setValid(false);
            String hashData = seatId + ":" + showId + ":" + orderId + ":" + Instant.now().toString();
            String ticketHash = HashUtil.generateHMAC(hashData);
            ticket.setHash(ticketHash);
            var refOrder = new OrderSummaryDto();
            refOrder.setId(orderId);
            ticket.setOrder(refOrder);
            // Note: @Peter R. ich habe das bissl geändert weil ich auch ein findSummaryById hatte.
            // Anundfürsich war die SummaryDto gedacht mit aggregtate Daten gefüllt zu sein, mein Request im Repository-Interface
            // hat hier dadurch an der Stelle null geliefert wenn noch kein Ticket drinnen war.
            // Das Replacement hier sollte aber prinzipiell gleichwertig sein für was du brauchst.
            // ticket.setOrder(orderDao.findSummaryById(orderId));
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Entity could not be found after validation", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error generating ticket hash", e);
        }
        return ticketDao.create(ticket);
    }

    @Override
    public void confirmTicket(TicketDetailsDto ticketDetailsDto) throws SchedulerException, DtoNotFoundException {
        if (ticketDetailsDto.isReserved()) {
            ticketInvalidationSchedulingService.rescheduleReservationInvalidationJobForConfirmedOrder(ticketDetailsDto);
        } else {
            ticketInvalidationSchedulingService.cancelReservationInvalidationJob(ticketDetailsDto.getId());
        }
        ticketDetailsDto.setValid(true);
        try {
            ticketDao.update(ticketDetailsDto);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public void setValidAllTicketsForOrder(long orderId) {
        ticketDao.setValidAllTicketsForOrder(orderId);
    }

    @Override
    public void changeTicketReserved(long ticketId, boolean setReserved) throws ValidationException {
        ticketDao.changeTicketReserved(ticketId, setReserved);
    }

    @Override
    public void deleteTicket(long ticketId) throws ValidationException {
        try {
            ticketDao.deleteById(ticketId);
        } catch (EntityNotFoundException ignored) {
            // ignored
        }
    }
}
