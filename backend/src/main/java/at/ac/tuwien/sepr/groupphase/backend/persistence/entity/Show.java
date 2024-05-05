package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Show extends AbstractEntity {
    @Column(name = "dateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;

    @ManyToMany(mappedBy = "shows")
    private List<Artist> artists;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    public Show(LocalDateTime dateTime, List<Artist> artists, Event event) {
        this.dateTime = dateTime;
        this.artists = artists;
        this.event = event;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Show() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Show show = (Show) o;
        return Objects.equals(dateTime, show.dateTime) && Objects.equals(artists, show.artists) && Objects.equals(event, show.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, artists, event);
    }

    @Override
    public String toString() {
        return "Show{" + "dateTime=" + dateTime + ", artists=" + artists + ", event=" + event + '}';
    }
}
