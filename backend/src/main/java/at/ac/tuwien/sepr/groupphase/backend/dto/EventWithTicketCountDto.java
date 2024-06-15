package at.ac.tuwien.sepr.groupphase.backend.dto;

public class EventWithTicketCountDto {
    private Long eventId;
    private String title;
    private String description;
    private Long ticketCount;


    public EventWithTicketCountDto(Long eventId, String title, String description, long ticketCount) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.ticketCount = ticketCount;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public Long getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Long ticketCount) {
        this.ticketCount = ticketCount;
    }
}