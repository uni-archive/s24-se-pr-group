package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ShowServiceImpl implements ShowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ShowDao dao;
    private final EventDao eventDao;

    public ShowServiceImpl(ShowDao dao, EventDao eventDao) {
        this.eventDao = eventDao;
        this.dao = dao;
    }

    @Override
    @Transactional
    public ResponseEntity<String> createShow(ShowCreationDto creationDto) {
        try {
            EventDto sh = eventDao.findById(creationDto.getEventid());
            ShowDto dto = new ShowDto().setDateTime(creationDto.getDateTime()).setEventid(sh.getId());
            dao.create(dto);
            return ResponseEntity.ok("\"Vorführung erfolreich erstellt.\"");
        } catch (Exception ex) {
            LOGGER.error("ERROR occurred: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("\"Erstellung gescheitert.\"");
        }
    }

    @Override
    public List<ShowListDto> getShowsByEventId(long eventid) throws EntityNotFoundException {
        return dao.getShowsByEventId(eventid);
    }

    @Override
    public List<ShowDto> getAllShows() {
        return dao.getAllShows();
    }

    @Override
    public List<ShowListDto> searchShows(ShowSearchDto searchDto) throws EntityNotFoundException {
        return dao.searchShows(searchDto);
    }

    @Override
    public Page<ShowDto> findByLocation(Long locationId, boolean onlyFutureShows, Pageable pageable) {
        return dao.findByLocationId(locationId, pageable);
    }
}
