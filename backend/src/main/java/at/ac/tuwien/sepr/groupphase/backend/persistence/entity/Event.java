package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Event extends AbstractEntity {

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION", length = 4096)
    private String description;

    @Column(name = "EVENT_TYPE")
    private EventType eventType;

    // in Seconds
    @Column(name = "DURATION")
    private Long duration;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Show> shows;

    @Override
    public String toString() {
        return "Event{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", eventType=" + eventType +
            ", duration=" + duration +
            "} " + super.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public EventType getEventType() {
        return eventType;
    }


    public Long getDuration() {
        return duration;
    }


    public List<Show> getShows() {
        return shows;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public Event setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public Event setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public Event setShows(List<Show> shows) {
        this.shows = shows;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return title.equals(event.title) && description.equals(event.description) && eventType == event.eventType && duration.equals(event.duration);
    }


}
