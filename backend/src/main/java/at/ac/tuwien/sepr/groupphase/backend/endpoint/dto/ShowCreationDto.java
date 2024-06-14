package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;

import java.time.LocalDateTime;
import java.util.List;

public class ShowCreationDto {
    private LocalDateTime dateTime;
    private EventDto event;

    private List<ArtistDto> artistList;

    private LocationDto location;

    private List<HallSectorShowCreationDto> sectorShowList;




    public LocalDateTime getDateTime() {
        return dateTime;
    }


    public ShowCreationDto setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }


    public ShowCreationDto(LocalDateTime dateTime, EventDto eventDto, List<ArtistDto> artists, LocationDto locationDto, List<HallSectorShowCreationDto> sectorShowList) {
        this.dateTime = dateTime;
        this.event = eventDto;
        this.artistList = artists;
        this.location = locationDto;
        this.sectorShowList = sectorShowList;
    }

    public ShowCreationDto setSectorShowList(List<HallSectorShowCreationDto> sectorShowList) {
        this.sectorShowList = sectorShowList;
        return this;
    }

    public List<HallSectorShowCreationDto> getSectorShowList() {
        return sectorShowList;
    }

    public ShowCreationDto setLocation(LocationDto location) {
        this.location = location;
        return this;
    }

    public LocationDto getLocation() {
        return location;
    }

    public ShowCreationDto setArtistList(List<ArtistDto> artistList) {
        this.artistList = artistList;
        return this;
    }

    public List<ArtistDto> getArtistList() {
        return artistList;
    }

    public EventDto getEvent() {
        return event;
    }

    public ShowCreationDto setEvent(EventDto event) {
        this.event = event;
        return this;
    }

    public ShowCreationDto() {
    }

    @Override
    public String toString() {
        return "ShowCreationDto{" +
            "dateTime=" + dateTime +
            ", event=" + event.getId() +
            ", artistList=" + artistList.size() +
            ", sectorShowList=" + sectorShowList.size() +
            '}';
    }
}
