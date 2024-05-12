package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;

public class ShowDto implements AbstractDto {
    private Long id;
    private LocalDateTime dateTime;

    // TODO: not needed right now
    // private List<ArtistDto> artists;
    private EventDto event;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public class ShowDtoBuilder {
        private LocalDateTime dateTime;
        private EventDto event;

        public ShowDtoBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public ShowDtoBuilder withEvent(EventDto event) {
            this.event = event;
            return this;
        }

        public ShowDto build() {
            var showDto = new ShowDto();
            showDto.setDateTime(dateTime);
            showDto.setEvent(event);
            return showDto;
        }
    }
}
