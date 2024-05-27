package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;

import java.time.LocalDateTime;
import java.util.List;

public class ShowDto implements AbstractDto {

    private long id;
    private LocalDateTime dateTime;
    private List<Artist> artistList;

    private EventDto event;

    @Override
    public Long getId() {
        return id;
    }

    public ShowDto(long id, LocalDateTime dateTime, List<Artist> artistList, EventDto event) {
        this.id = id;
        this.dateTime = dateTime;
        this.artistList = artistList;
        this.event = event;
    }

    public ShowDto() {
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }


    public ShowDto setId(long id) {
        this.id = id;
        return this;
    }

    public ShowDto setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ShowDto setArtistList(List<Artist> artistList) {
        this.artistList = artistList;
        return this;
    }

    public ShowDto setEvent(EventDto event) {
        this.event = event;
        return this;
    }

    public EventDto getEvent() {
        return event;
    }
}
