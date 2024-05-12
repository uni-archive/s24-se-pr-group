package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallSectorShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.HallSectorShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class HallSectorShowServiceImpl implements HallSectorShowService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallSectorShowDao hallSectorShowDao;

    public HallSectorShowServiceImpl(HallSectorShowDao hallSectorShowDao) {
        this.hallSectorShowDao = hallSectorShowDao;
    }

    @Override
    public List<HallSectorShowDto> findByShowId(long showId) {
        LOGGER.debug("Get hall-sectors for show. Show-ID: {}", showId);
        return hallSectorShowDao.findByShowId(showId);
    }

    @Override
    public HallSectorShowDto findByShowIdAndHallSectorId(long showId, long hallSectorId) throws EntityNotFoundException {
        LOGGER.debug("Get info for sector-show pairing. Show-ID: {}, Hall-Sector-ID: {}", showId, hallSectorId);
        return hallSectorShowDao.findByShowIdAndSectorId(showId, hallSectorId);
    }
}
