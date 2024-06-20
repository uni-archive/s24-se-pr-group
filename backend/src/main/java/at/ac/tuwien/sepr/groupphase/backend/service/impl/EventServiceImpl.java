package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventResponseMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private EventDao dao;

    public EventServiceImpl(EventDao eventDao) {
        this.dao = eventDao;
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        LOGGER.info("CREATING: {}", eventDto);
        return dao.create(eventDto);
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
    public EventDto getById(long id) throws DtoNotFoundException {
        try {
            return dao.findById(id);
        } catch (EntityNotFoundException e) {
            throw new DtoNotFoundException(e);
        }
    }

    @Override
    public List<EventDto> findByArtist(long artistId) {
        return dao.findByArtist(artistId);
    }

    @Override
    public List<EventWithTicketCountDto> getTop10EventsWithMostTickets() {
        return dao.getTop10EventsWithMostTickets();
    }
}
