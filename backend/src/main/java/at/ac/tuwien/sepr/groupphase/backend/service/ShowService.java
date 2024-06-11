package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ShowService {

    /**
     * Creates a new Show with the given parameters in the database.
     *
     * @param creationDto given parameters
     * @return rest response entity
     */
    String createShow(ShowDto creationDto, List<HallSectorShowDto> sectorShowList);

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

    /**
     * Get a show by its id.
     *
     * @param showId the id of the show
     * @return the show
     */
    ShowDto findById(Long showId) throws DtoNotFoundException;
}

