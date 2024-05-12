package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;

public class EventDto implements AbstractDto {

    private Long id;

    private String title;

    private String description;

    private EventType eventType;

    // in Seconds
    private Long duration;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public class EventDtoBuilder {
        private String title;
        private String description;
        private EventType eventType;
        private Long duration;

        public EventDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public EventDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EventDtoBuilder withEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public EventDtoBuilder withDuration(Long duration) {
            this.duration = duration;
            return this;
        }

        public EventDto createEventDto() {
            var eventDto = new EventDto();
            eventDto.setTitle(title);
            eventDto.setDescription(description);
            eventDto.setEventType(eventType);
            eventDto.setDuration(duration);
            return eventDto;
        }
    }
}
