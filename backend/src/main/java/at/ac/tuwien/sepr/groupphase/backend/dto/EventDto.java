package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;

import java.util.List;

public class EventDto implements AbstractDto {

    private long id;
    private String description;
    private long duration;
    private String title;
    private EventType eventType;
    private List<ShowDto> shows;

    @Override
    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<ShowDto> getShows() {
        return shows;
    }

    public EventDto setId(long id) {
        this.id = id;
        return this;
    }

    public EventDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventDto setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public EventDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventDto setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventDto setShows(List<ShowDto> shows) {
        this.shows = shows;
        return this;
    }

    public EventDto(long id, String description, long duration, String title, EventType type, List<ShowDto> shows) {
        this.id = id;
        this.description = description;
        this.duration = duration;
        this.title = title;
        this.eventType = type;
        this.shows = shows;
    }

    public EventDto() {
    }

    @Override
    public String toString() {
        return "EventDto{"
            + "id=" + id
            + ", description='" + description + '\''
            + ", duration=" + duration
            + ", Title='" + title + '\''
            + ", type=" + eventType
            + ", shows=" + shows
            + '}';
    }
}
