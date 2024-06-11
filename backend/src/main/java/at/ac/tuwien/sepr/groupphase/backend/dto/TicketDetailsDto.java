package at.ac.tuwien.sepr.groupphase.backend.dto;

public class TicketDetailsDto implements AbstractDto {

    private Long id;
    private String hash;
    private boolean reserved;
    private boolean valid;
    private HallSpotDto hallSpot;
    private ShowDto show;
    private OrderDetailsDto order;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public HallSpotDto getHallSpot() {
        return hallSpot;
    }

    public void setHallSpot(HallSpotDto hallSpot) {
        this.hallSpot = hallSpot;
    }

    public ShowDto getShow() {
        return show;
    }

    public void setShow(ShowDto show) {
        this.show = show;
    }

    public OrderDetailsDto getOrder() {
        return order;
    }

    public TicketDetailsDto setOrder(OrderDetailsDto order) {
        this.order = order;
        return this;
    }

    public class TicketDetailsDtoBuilder {

        private String hash;
        private boolean reserved;
        private boolean valid;
        private HallSpotDto hallSpot;
        private ShowDto show;
        private OrderDetailsDto orderDto;

        public TicketDetailsDtoBuilder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public TicketDetailsDtoBuilder withReserved(boolean reserved) {
            this.reserved = reserved;
            return this;
        }

        public TicketDetailsDtoBuilder withValid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public TicketDetailsDtoBuilder withHallSpot(HallSpotDto hallSpot) {
            this.hallSpot = hallSpot;
            return this;
        }

        public TicketDetailsDtoBuilder withShow(ShowDto show) {
            this.show = show;
            return this;
        }

        public TicketDetailsDtoBuilder withOrder(OrderDetailsDto orderDto) {
            this.orderDto = orderDto;
            return this;
        }

        public TicketDetailsDto build() {
            var ticketDetailsDto = new TicketDetailsDto();
            ticketDetailsDto.setHash(hash);
            ticketDetailsDto.setReserved(reserved);
            ticketDetailsDto.setValid(valid);
            ticketDetailsDto.setHallSpot(hallSpot);
            ticketDetailsDto.setShow(show);
            ticketDetailsDto.setOrder(orderDto);
            return ticketDetailsDto;
        }
    }
}
