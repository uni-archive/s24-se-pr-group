package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @OneToMany(mappedBy = "event")
    private List<Show> shows;

    @Override
    public String toString() {
        return "Event{" + "title='" + title + '\'' + ", description='" + description + '\'' + ", eventType=" + eventType + ", duration=" + duration + '}';
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

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}
