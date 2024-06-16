package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ShowService {

    /**
     * Creates a new Show with the given parameters in the database.
     *
     * @param creationDto given parameters
     * @return rest response entity
     */
    ResponseEntity<String> createShow(ShowCreationDto creationDto);

    /**
     * Get a list of all shows.
     *
     * @return ShowDto
     */
    List<ShowDto> getAllShows();

    /**
     * Get list of Shows that are part of a certain event.
     *
     * @param eventid the id of the event
     * @return list of the shows
     */
    List<ShowListDto> getShowsByEventId(long eventid) throws EntityNotFoundException;

    HallPlanDto getHallPlanByShowId(Long showId);

    /**
     * Get a list of ShowDtos matching the search criteria.
     *
     * @param searchDto contains search chriteria
     * @return list of ShowDtos
     */
    List<ShowListDto> searchShows(ShowSearchDto searchDto) throws EntityNotFoundException;


    /**
     * Get all shows for a given location.
     *
     * @return all shows that are at the
     */
    Page<ShowDto> findByLocation(Long locationId, boolean onlyFutureShows, Pageable pageable);

    ShowDto getById(Long id) throws EntityNotFoundException;
}

