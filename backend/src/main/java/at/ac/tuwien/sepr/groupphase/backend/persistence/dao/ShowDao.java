package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowListDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import jakarta.transaction.Transactional;
import org.hibernate.action.internal.EntityActionVetoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ShowDao extends AbstractDao<Show, ShowDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artrepo;
    private final EventDao eventDao;
    private final ArtistMapper artistMapper;
    private final EventMapper eventMapper;

    protected ShowDao(ShowRepository repository, EventMapper eventMapper, EventDao dao, ArtistRepository artrepo,
        ArtistMapper artmapper, ShowMapper mapper) {
        super(repository, mapper);
        this.artrepo = artrepo;
        this.artistMapper = artmapper;
        this.eventDao = dao;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public List<ShowListDto> getShowsByEventId(long eventid) throws EntityNotFoundException {
        List<ShowListDto> list = new ArrayList<>();
        for (ShowDto showDto : mapper.toDto(((ShowRepository) repository).getShowByEventId(eventid))) {
            ShowListDto showListDto = new ShowListDto()
                .setId(showDto.getId())
                .setDateTime(showDto.getDateTime())
                .setEventid(showDto.getEventid())
                .setStartingPrice(((ShowRepository) repository).getStartingPriceOfShow(showDto.getId()))
                .setArtistList(artistMapper.toDto(artrepo.findByShowId(showDto.getId())))
                .setEventName(eventDao.findById(eventid).getTitle());
            list.add(showListDto);
        }
        return list;
    }

    public Page<ShowDto> findByLocationId(Long locationId, Pageable pageable) {
        return ((ShowRepository) repository).findByLocationId(locationId, pageable).map(mapper::toDto);
    }

    @Transactional
    public List<ShowDto> getAllShows() {
        return mapper.toDto(((ShowRepository) repository).getAllShows());
    }

    @Transactional
    public List<ShowListDto> searchShows(ShowSearchDto searchDto) throws EntityNotFoundException {
        var showlist = mapper.toDto(((ShowRepository) repository)
            .findShowsBySearchDto(searchDto.getDateTime(), searchDto.getPrice(), searchDto.getEventId()));
        LOGGER.info("SHOWLIST({})", showlist);
        List<ShowListDto> list = new ArrayList<>();
        for (ShowDto showDto : showlist) {
            ShowListDto showListDto = new ShowListDto()
                .setId(showDto.getId())
                .setDateTime(showDto.getDateTime())
                .setEventid(showDto.getEventid())
                .setStartingPrice(((ShowRepository) repository).getStartingPriceOfShow(showDto.getId()))
                .setArtistList(artistMapper.toDto(artrepo.findByShowId(showDto.getId())))
                .setEventName(eventDao.findById(showDto.getEventid()).getTitle());
            list.add(showListDto);
        }
        return list;
    }
}