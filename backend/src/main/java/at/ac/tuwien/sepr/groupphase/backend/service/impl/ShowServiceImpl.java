package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShowHallPlanResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.HallPlanDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.ShowDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.TicketDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.HallSectorShowService;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.HallSectorShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import jakarta.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ShowServiceImpl implements ShowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ShowDao dao;
    private final HallSectorShowRepository sectorShowRepository;
    private final HallSectorShowMapper sectorShowMapper;

    public ShowServiceImpl(ShowDao dao, HallSectorShowRepository sectorShowRepository,
        HallSectorShowMapper sectorShowMapper) {
        this.dao = dao;
        this.sectorShowRepository = sectorShowRepository;
        this.sectorShowMapper = sectorShowMapper;
    }

    @Override
    @Transactional
    public String createShow(ShowDto creationDto, List<HallSectorShowDto> sectorShowList) {
        try {

            var showDto = dao.create(creationDto);
            sectorShowList.forEach(sector ->
                sectorShowRepository.save(
                    sectorShowMapper.toEntity(new HallSectorShowDto()
                        .setSector(sector.getSector())
                        .setShow(showDto)
                        .setPrice(sector.getPrice() * 100))
                )
            );
            return "\"Vorführung erfolgreich erstellt.\"";
        } catch (Exception ex) {
            LOGGER.error("ERROR occurred: {}", ex.getMessage(), ex);
            return "\"Erstellung gescheitert.\"";
        }
    }

    @Override
    public List<ShowListDto> getShowsByEventId(long eventid) throws DtoNotFoundException {
        try {
            return dao.getShowsByEventId(eventid);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public List<ShowDto> getAllShows() {
        return dao.getAllShows();
    }

    @Override
    public HallPlanDto getHallPlanByShowId(Long showId) {
        var hallPlan = dao.getHallPlanByShowId(showId);
        return hallPlan;
    }

    @Override
    public Page<ShowListDto> searchShows(ShowSearchDto searchDto) throws DtoNotFoundException {
        if (Objects.isNull(searchDto.getPageable())) {
            PageRequest defaultPage = PageRequest.of(0, 15, Sort.by("dateTime"));
            searchDto.setPageable(defaultPage);
        }

        try {
            return dao.searchShows(searchDto);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    @Transactional
    public Page<ShowDto> findByLocation(Long locationId, boolean onlyFutureShows, Pageable pageable) {
        return dao.findByLocationId(locationId, pageable);
    }

    @Override
    @Transactional
    public Page<ShowDto> findByArtistId(Long artistId, boolean onlyFutureShows, Pageable pageable) {
        return dao.findByArtistId(artistId, pageable);
    }

    @Override
    @Transactional
    public ShowDto getById(Long id) throws DtoNotFoundException {
        try {
            return dao.findById(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Transactional
    @Override
    public ShowDto findById(Long showId) throws DtoNotFoundException {
        try {
            return dao.findById(showId);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }
}
