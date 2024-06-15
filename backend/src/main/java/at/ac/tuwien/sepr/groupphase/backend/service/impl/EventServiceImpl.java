package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private EventDao dao;
    private EventResponseMapper mapper;

    public EventServiceImpl(EventDao eventDao, EventResponseMapper mapper) {
        this.dao = eventDao;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<EventDto> createEvent(EventCreationDto eventDto) {
        try {
            LOGGER.info("CREATING: {}", mapper.toDto(eventDto));
            return ResponseEntity.ok(dao.create(mapper.toDto(eventDto)));
        } catch (Exception ex) {
            LOGGER.info("ERROR occurred: {}", ex);
            return ResponseEntity.internalServerError().build();
        }

    }


    @Override
    public List<EventDto> searchEvents(EventSearchDto searchOptions) {
        return dao.searchEvents(searchOptions);
    }

    @Override
    public List<EventDto> getAllEvents() {
        return dao.allEvents();
    }

    @Override
    public EventDto getById(long id) throws EntityNotFoundException {
        return dao.findById(id);
    }

    @Override
    public List<EventDto> findByArtist(long artistId) {
        return dao.findByArtist(artistId);
    }

    @Override
    public List<EventWithTicketCountDto> getTop10EventsWithMostTickets(PageRequest pageRequest) {
        return dao.getTop10EventsWithMostTickets(pageRequest);
    }
}
