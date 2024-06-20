package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;

public interface EventWithTicketCountProjection {
    Event getEvent();

    long getTicketCount();
}