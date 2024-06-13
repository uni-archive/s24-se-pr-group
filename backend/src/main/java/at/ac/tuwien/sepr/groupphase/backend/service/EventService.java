package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EventService {

    /**
     * Creates a new Event with the given Parameters in the Database.
     *
     * @param eventDto creation dto
     */
    ResponseEntity<EventDto> createEvent(EventCreationDto eventDto);

    /**
     * Returns a list of EventDTOs that fit the given search criteria.
     *
     * @param searchOptions search parameters dto
     */
    List<EventDto> searchEvents(EventSearchDto searchOptions);

    /**
     * Returns a list of all Events in the form of EventDTOs.
     *
     * @return List of EventDtos
     */
    List<EventDto> getAllEvents();

    List<EventDto> findByArtist(long artistId);

    /**
     * Fetches the EventDto corresponding to id.
     *
     * @param id event id
     * @return ResponseEntity with EventDto
     */
    EventDto getById(long id) throws EntityNotFoundException;

    @Transactional
    List<EventDto> getTop10EventsWithMostTickets();
}
