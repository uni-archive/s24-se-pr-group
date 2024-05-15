package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;

import java.util.List;

/**
 * A sector may be used in multiple shows and a show contains multiple sectors.
 * This interface exposes an API that allows to act on these pairings.
 */
public interface HallSectorShowService {

    /**
     * Searches for all sectors that are used in a given show.
     *
     * @param showId the id of the show.
     * @return A list of all sectors used in a show.
     */
    List<HallSectorShowDto> findByShowId(long showId);

    /**
     * Gets the information for a (sector, show) tuple.
     *
     * @param showId the id of the show.
     * @param hallSectorId the id of the sector.
     * @return {@link HallSectorShowDto}
     *
     */
    HallSectorShowDto findByShowIdAndHallSectorId(long showId, long hallSectorId) throws EntityNotFoundException;
}
