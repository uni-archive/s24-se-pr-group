package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.ShowDto;
import at.ac.tuwien.sepr.groupphase.backend.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class EventDao extends AbstractDao<Event, EventDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private BaseEntityMapper<Show, ShowDto> showMapper;

    protected EventDao(EventRepository repository, EventMapper mapper, ShowMapper showMapper) {
        super(repository, mapper);
        this.showMapper = showMapper;
    }

    @Transactional
    public List<EventDto> searchEvents(EventSearchDto searchDto) {
        return mapper.toDto(((EventRepository) repository).findBySearchDto(searchDto.getDauer() * 60, searchDto.getTyp(), searchDto.getTextSearch().toUpperCase()));
    }

    @Transactional
    public List<EventDto> allEvents() {
        return mapper.toDto(((EventRepository) repository).getAllEvents());
    }

    @Transactional
    @Override
    public EventDto findById(Long id) throws EntityNotFoundException {
        return super.findById(id);
    }

    @Transactional
    public List<EventDto> findByArtist(long artistId) {
        return mapper.toDto(((EventRepository) repository).findByArtist(artistId));
    }
}
