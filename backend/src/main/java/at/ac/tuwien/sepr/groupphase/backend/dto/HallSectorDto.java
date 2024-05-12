package at.ac.tuwien.sepr.groupphase.backend.dto;

public class HallSectorDto implements AbstractDto {
    private Long id;

    private HallSectorShowDto hallSectorShow;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HallSectorShowDto getHallSectorShow() {
        return hallSectorShow;
    }

    public void setHallSectorShow(HallSectorShowDto hallSectorShow) {
        this.hallSectorShow = hallSectorShow;
    }

    public class HallSectorDtoBuilder {
        private HallSectorShowDto hallSectorShow;

        public HallSectorDtoBuilder withHallSectorShow(HallSectorShowDto hallSectorShow) {
            this.hallSectorShow = hallSectorShow;
            return this;
        }

        public HallSectorDto build() {
            var hallSector = new HallSectorDto();
            hallSector.setHallSectorShow(hallSectorShow);
            return hallSector;
        }
    }
}
