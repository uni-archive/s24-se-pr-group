package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;

import java.time.LocalDateTime;
import java.util.List;

public class ShowCreationDto {
    private LocalDateTime dateTime;
    private long eventid;

    private List<ArtistDto> artists;

    //TODO: Location


    public LocalDateTime getDateTime() {
        return dateTime;
    }


    public ShowCreationDto setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ShowCreationDto setEventid(long eventid) {
        this.eventid = eventid;
        return this;
    }

    public ShowCreationDto(LocalDateTime dateTime, long eventid, List<ArtistDto> artists) {
        this.dateTime = dateTime;
        this.eventid = eventid;
        this.artists = artists;
    }

    public ShowCreationDto setArtists(List<ArtistDto> artists) {
        this.artists = artists;
        return this;
    }

    public List<ArtistDto> getArtists() {
        return artists;
    }

    public long getEventid() {
        return eventid;
    }

    public ShowCreationDto() {
    }
}
