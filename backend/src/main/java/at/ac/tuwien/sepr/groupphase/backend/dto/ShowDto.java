package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;

import java.time.LocalDateTime;
import java.util.List;

public class ShowDto implements AbstractDto {

    private Long id;
    private LocalDateTime dateTime;
    private List<ArtistDto> artistList;

    private LocationDto location;

    private EventDto event;



    @Override
    public Long getId() {
        return id;
    }

    public ShowDto(Long id, LocalDateTime dateTime, List<ArtistDto> artistList, EventDto event, LocationDto location) {
        this.id = id;
        this.dateTime = dateTime;
        this.artistList = artistList;
        this.event = event;
        this.location = location;
    }

    public ShowDto() {
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<ArtistDto> getArtistList() {
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

    public ShowDto setArtistList(List<ArtistDto> artistList) {
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

    @Override
    public String toString() {
        return "ShowDto{" +
            "id=" + id +
            ", dateTime=" + dateTime +
            ", artistList=" + artistList +
            ", event=" + event.getTitle() +
            '}';
    }
}
