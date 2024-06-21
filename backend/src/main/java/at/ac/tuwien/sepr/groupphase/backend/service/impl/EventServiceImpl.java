package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventWithTicketCountDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.EventDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

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
    public Page<EventDto> searchEvents(EventSearchDto searchOptions) {
        if (Objects.isNull(searchOptions.getPageable())) {
            PageRequest defaultPage = PageRequest.of(0, 15, Sort.by("title", "description"));
            searchOptions.setPageable(defaultPage);
        }

        EventType type = null;
        if (searchOptions.getTyp() != null) {
            type = EventType.valueOf(searchOptions.getTyp().name());
        }
        return dao.searchEvents(searchOptions.getTextSearch(), searchOptions.getDauer(), type, searchOptions.getPageable());
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
