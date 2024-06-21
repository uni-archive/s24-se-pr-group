package at.ac.tuwien.sepr.groupphase.backend.dto;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public class ShowSearchDto {

    private LocalDateTime dateTime; //Date has to be exact, time is the earliest start time
    private Long price; // +- 30%

    private Long eventId;

    private Long location;

    private Pageable pageable;

    public ShowSearchDto() {
    }

    public ShowSearchDto(LocalDateTime dateTime, long price, long eventId, long location) {
        this.dateTime = dateTime;
        this.price = price;
        this.eventId = eventId;
        this.location = location;
    }

    public ShowSearchDto setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ShowSearchDto setPrice(long price) {
        this.price = price;
        return this;
    }

    public ShowSearchDto setEventId(long eventId) {
        this.eventId = eventId;
        return this;
    }

    public ShowSearchDto setLocation(long location) {
        this.location = location;
        return this;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getPrice() {
        return price;
    }

    public long getEventId() {
        return eventId;
    }

    public long getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "ShowSearchDto{"
            + "dateTime=" + dateTime
            + ", price=" + price
            + ", eventId=" + eventId
            + ", location=" + location
            + '}';
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
