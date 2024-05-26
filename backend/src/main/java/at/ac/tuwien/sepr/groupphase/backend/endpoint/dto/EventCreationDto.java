package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;

public class EventCreationDto {

    private String description;
    private long duration;
    private String title;
    private EventType eventType;

    @Override
    public String toString() {
        return "EventCreationDto{"
            + "description='" + description + '\''
            + ", duration=" + duration
            + ", Title='" + title + '\''
            + ", type=" + eventType
            + '}';
    }

    public EventCreationDto(String description, long duration, String title, EventType type) {
        this.description = description;
        this.duration = duration;
        this.title = title;
        this.eventType = type;
    }

    public EventCreationDto() {
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

    public EventCreationDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventCreationDto setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public EventCreationDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventCreationDto setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }
}
