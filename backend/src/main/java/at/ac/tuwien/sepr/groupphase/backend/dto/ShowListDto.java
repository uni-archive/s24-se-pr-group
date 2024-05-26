package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;

import java.time.LocalDateTime;
import java.util.List;

public class ShowListDto implements AbstractDto {

    private long id;

    private LocalDateTime dateTime;
    private List<ArtistDto> artistList;

    private long eventid;

    private long startingPrice;

    private String eventName;

    @Override
    public Long getId() {
        return id;
    }

    public ShowListDto() {
    }

    public ShowListDto(long id, LocalDateTime dateTime, List<ArtistDto> artistList, long eventid, long startingPrice, String eventName) {
        this.id = id;
        this.dateTime = dateTime;
        this.artistList = artistList;
        this.eventid = eventid;
        this.startingPrice = startingPrice;
        this.eventName = eventName;
    }

    public ShowListDto setId(long id) {
        this.id = id;
        return this;
    }

    public ShowListDto setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ShowListDto setArtistList(List<ArtistDto> artistList) {
        this.artistList = artistList;
        return this;
    }

    public ShowListDto setEventid(long eventid) {
        this.eventid = eventid;
        return this;
    }

    public ShowListDto setStartingPrice(long startingPrice) {
        this.startingPrice = startingPrice;
        return this;
    }

    public ShowListDto setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<ArtistDto> getArtistList() {
        return artistList;
    }

    public long getEventid() {
        return eventid;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public String getEventName() {
        return eventName;
    }
}
