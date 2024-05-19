package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.TicketValidator;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class TicketServiceImplTest implements TestData {

    @MockBean
    private TicketValidator ticketValidator;

    @Captor
    private ArgumentCaptor<TicketDetailsDto> ticketDto;

    @Autowired
    private TicketServiceImpl ticketService;

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

    private static Ticket testTicketValidReserved;
    private static Ticket testTicketInvalidReserved;

    private static Ticket testTicketValidNonReserved;
    private static Ticket testTicketInvalidNonReserved;

    @BeforeEach
    void setup() {
        var event = new Event();
        var show = new Show();
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
        sectorShow.setPrice(50);
        hallSectorShowRepository.save(sectorShow);

        testTicketValidReserved = new Ticket();
        testTicketValidReserved.setHash("hash");
        testTicketValidReserved.setReserved(true);
        testTicketValidReserved.setValid(true);
        testTicketValidReserved.setHallSpot(hallSpot1);
        testTicketValidReserved.setShow(show);
        ticketRepository.save(testTicketValidReserved);

        testTicketInvalidReserved = new Ticket();
        testTicketInvalidReserved.setHash("hash");
        testTicketInvalidReserved.setReserved(true);
        testTicketInvalidReserved.setValid(false);
        testTicketInvalidReserved.setHallSpot(hallSpot2);
        testTicketInvalidReserved.setShow(show);
        ticketRepository.save(testTicketInvalidReserved);

        testTicketValidNonReserved = new Ticket();
        testTicketValidNonReserved.setHash("hash");
        testTicketValidNonReserved.setReserved(false);
        testTicketValidNonReserved.setValid(true);
        testTicketValidNonReserved.setHallSpot(hallSpot3);
        testTicketValidNonReserved.setShow(show);
        ticketRepository.save(testTicketValidNonReserved);

        testTicketInvalidNonReserved = new Ticket();
        testTicketInvalidNonReserved.setHash("hash");
        testTicketInvalidNonReserved.setReserved(false);
        testTicketInvalidNonReserved.setValid(false);
        testTicketInvalidNonReserved.setHallSpot(hallSpot4);
        testTicketInvalidNonReserved.setShow(show);
        ticketRepository.save(testTicketInvalidNonReserved);
    }

    @AfterEach
    void teardown() {
        ticketRepository.deleteAll();
        hallSectorShowRepository.deleteAll();
        hallSpotRepository.deleteAll();
        hallSectorRepository.deleteAll();
        hallPlanRepository.deleteAll();
        showRepository.deleteAll();
        eventRepository.deleteAll();
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
}
