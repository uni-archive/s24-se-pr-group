package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSpotDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.OrderDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.SectorTicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSectorDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSpotDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.OrderDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.HashUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.HallSectorShowService;
import at.ac.tuwien.sepr.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.TicketValidator;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketDao ticketDao;
    private final HallSpotDao hallSpotDao;
    private final ShowDao showDao;

    private final TicketValidator ticketValidator;

    private final HallSectorShowService hallSectorShowService;
    private final OrderDao orderDao;

    private final OrderService orderService;
    private final TicketInvalidationSchedulingService ticketInvalidationSchedulingService;
    private final HallSectorDao hallSectorDao;

    public TicketServiceImpl(TicketDao ticketDao, HallSpotDao hallSpotDao, ShowDao showDao,
                             TicketValidator ticketValidator, HallSectorShowService hallSectorShowService, OrderDao orderDao,
                             @Lazy OrderService orderService, TicketInvalidationSchedulingService ticketInvalidationSchedulingService, HallSectorDao hallSectorDao) {
        this.ticketDao = ticketDao;
        this.hallSpotDao = hallSpotDao;
        this.showDao = showDao;
        this.ticketValidator = ticketValidator;
        this.hallSectorShowService = hallSectorShowService;
        this.orderDao = orderDao;
        this.orderService = orderService;
        this.ticketInvalidationSchedulingService = ticketInvalidationSchedulingService;
        this.hallSectorDao = hallSectorDao;
    }

    @Override
    public TicketDetailsDto findById(long id) throws DtoNotFoundException {
        try {
            var ticket = ticketDao.findById(id);
            var sectorShow = loadSectorShowForTicket(ticket);
            ticket.getHallSpot().getSector().setHallSectorShow(sectorShow);
            return ticket;
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public List<TicketDetailsDto> findForUserById(long userId) {
        var tickets = ticketDao.findByUserId(userId);
        for (var ticket : tickets) {
            ticket.getHallSpot().getSector().setHallSectorShow(loadSectorShowForTicket(ticket));
        }
        return tickets;
    }

    @Override
    public HallSectorShowDto loadSectorShowForTicket(TicketDetailsDto ticket) {
        try {
            // handle cyclic dependencies between sectors and shows that apply to each ticket
            return hallSectorShowService.findByShowIdAndHallSectorId(
                ticket.getShow().getId(),
                ticket.getHallSpot().getSector().getId()
            );
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
            ticketInvalidationSchedulingService.cancelReservationInvalidationJob(ticket.getHash());
            ticketDao.cancelReservedTicket(ticket.getId());
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public void invalidateAllTicketsForOrder(long orderId) {
        ticketDao.invalidateAllTicketsForOrder(orderId);
    }

    @Override
    @Transactional
    public TicketDetailsDto addTicketToOrder(TicketAddToOrderDto ticket, ApplicationUserDto user)
        throws ValidationException, ForbiddenException {
        try {
            var order = orderDao.findById(ticket.orderId());
            if (!Objects.equals(user.getId(), order.getCustomer().getId())) {
                // Create custom exception to delete the cookie if the order does not exist
                throw new ForbiddenException();
            }
        } catch (EntityNotFoundException e) {
            throw new ForbiddenException();
        }

        TicketDetailsDto ticketDetailsDto = createTicket(ticket);
        try {
            ticketInvalidationSchedulingService.scheduleReservationInvalidationsForNewlyAddedTicket(ticketDetailsDto);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Could not schedule reservation invalidation job: " + e.getMessage(), e);
        }
        return ticketDetailsDto;
    }

    @Override
    public TicketDetailsDto addSectorTicketToOrder(SectorTicketAddToOrderDto ticket, ApplicationUserDto user)
        throws ValidationException, ForbiddenException {
        try {
            var order = orderDao.findById(ticket.orderId());
            if (!Objects.equals(user.getId(), order.getCustomer().getId())) {
                throw new ForbiddenException();
            }
        } catch (EntityNotFoundException e) {
            throw new ForbiddenException();
        }
        List<HallSpotDto> availableSpots;
        try {
            availableSpots = ticketDao.findFreeSpotForSector(ticket.showId(), ticket.sectorId());
            if (availableSpots.isEmpty()) {
                throw new ValidationException("No free spots available for sector");
            }
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No free spots available for sector");
        }

        var random = new Random();
        TicketAddToOrderDto ticketAddToOrderDto =
            new TicketAddToOrderDto(availableSpots.get(random.nextInt(availableSpots.size())).getId(), ticket.orderId(), ticket.showId(),
                ticket.reservationOnly());

        TicketDetailsDto ticketDetailsDto = createTicket(ticketAddToOrderDto);
        try {
            ticketInvalidationSchedulingService.scheduleReservationInvalidationsForNewlyAddedTicket(ticketDetailsDto);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Could not schedule reservation invalidation job" + e.getMessage(), e);
        }
        return ticketDetailsDto;
    }


    private TicketDetailsDto createTicket(TicketAddToOrderDto createTicket)
        throws ValidationException {
        TicketDetailsDto ticket = new TicketDetailsDto();
        ticketValidator.validateForCreate(createTicket);
        try {
            ticket.setHallSpot(hallSpotDao.findById(createTicket.spotId()));
            ticket.setShow(showDao.findById(createTicket.showId()));
            ticket.setReserved(createTicket.reservationOnly());
            ticket.setValid(false);
            String hashData = createTicket.spotId() + ":" + createTicket.showId() + ":" + createTicket.orderId() + ":" + Instant.now().toString();
            String ticketHash = HashUtil.generateHMAC(hashData);
            ticket.setHash(ticketHash);
            var refOrder = new OrderDetailsDto();
            refOrder.setId(createTicket.orderId());
            ticket.setOrder(refOrder);
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Entity could not be found after validation", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error generating ticket hash", e);
        }
        return ticketDao.create(ticket);
    }

    @Override
    @Transactional
    public void confirmTicket(TicketDetailsDto ticketDetailsDto) throws SchedulerException, DtoNotFoundException {
        if (ticketDetailsDto.isReserved()) {
            ticketInvalidationSchedulingService.rescheduleReservationInvalidationJobForConfirmedOrder(ticketDetailsDto);
        } else {
            ticketInvalidationSchedulingService.cancelReservationInvalidationJob(ticketDetailsDto.getHash());
            ticketDetailsDto.setValid(true);
            try {
                ticketDao.update(ticketDetailsDto);
            } catch (EntityNotFoundException e) {
                throw new DtoNotFoundException(e);
            }
        }
    }

    @Override
    public TicketDetailsDto findByHash(String ticketHash) throws DtoNotFoundException {
        try {
            return ticketDao.findByHash(ticketHash);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public void changeTicketReserved(long ticketId, boolean setReserved, ApplicationUserDto user)
        throws ValidationException, DtoNotFoundException, ForbiddenException {
        TicketDetailsDto ticket;
        try {
            ticket = ticketDao.findById(ticketId);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }

        try {
            var order = orderService.findByIdForJobRefresh(ticket.getOrder().getId());
            if (!order.getCustomer().getId().equals(user.getId())) {
                throw new ForbiddenException();
            }

            ticketValidator.validateForChangeTicketReserved(ticketId, order);

            ticketDao.changeTicketReserved(ticketId, setReserved);
        } catch (DtoNotFoundException e) {
            throw new IllegalStateException("Ticket has invalid order id");
        }
    }

    @Override
    public void deleteTicket(long ticketId, ApplicationUserDto user) throws ValidationException, ForbiddenException {
        try {
            var ticket = ticketDao.findById(ticketId);
            var order = orderDao.findById(ticket.getOrder().getId());

            if (!order.getCustomer().getId().equals(user.getId())) {
                throw new ForbiddenException();
            }

            ticketValidator.validateForDelete(ticketId, order);
            ticketDao.deleteById(ticketId);
        } catch (EntityNotFoundException ignored) {
            // ignored
        }
    }

    @Override
    public List<TicketDetailsDto> findForShowById(long showId) {
        var tickets = ticketDao.findForShowById(showId);
        for (var ticket : tickets) {
            ticket.getHallSpot().getSector().setHallSectorShow(loadSectorShowForTicket(ticket));
        }
        return tickets;
    }

    @Override
    public Page<TicketDetailsDto> search(TicketSearchDto ticketSearchDto) {
        return ticketDao.search(ticketSearchDto);
    }

    @Override
    public void validateTicket(long id) throws DtoNotFoundException, ValidationException {
        try {
            TicketDetailsDto ticket = ticketDao.findById(id);
            ticketValidator.validateForValidation(ticket);
            ticketDao.validateTicketById(id);
            ticketInvalidationSchedulingService.cancelReservationInvalidationJob(ticket.getHash());
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }
}
