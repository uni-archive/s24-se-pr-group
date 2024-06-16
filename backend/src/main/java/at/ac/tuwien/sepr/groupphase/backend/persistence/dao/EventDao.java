package at.ac.tuwien.sepr.groupphase.backend.persistence.dao;

import at.ac.tuwien.sepr.groupphase.backend.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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


    public List<EventWithTicketCountDto> getTop10EventsWithMostTickets() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<EventWithTicketCountProjection> results = ((EventRepository) repository)
            .findTop10ByOrderByTicketCountDesc(thirtyDaysAgo, PageRequest.of(0, 10));

        return results.stream()
            .map(projection -> {
                Event event = projection.getEvent();
                long ticketCount = projection.getTicketCount();
                return new EventWithTicketCountDto(event.getId(), event.getTitle(), event.getDescription(), ticketCount);
            })
            .collect(Collectors.toList());
    }
}