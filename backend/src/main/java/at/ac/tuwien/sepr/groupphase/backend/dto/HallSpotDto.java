package at.ac.tuwien.sepr.groupphase.backend.dto;

public class HallSpotDto implements AbstractDto {
    private Long id;

    private HallSectorDto sector;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HallSectorDto getSector() {
        return sector;
    }

    public void setSector(HallSectorDto sector) {
        this.sector = sector;
    }

    public class HallSpotDtoBuilder {
        private HallSectorDto sector;

        public HallSpotDtoBuilder withHallSector(HallSectorDto sector) {
            this.sector = sector;
            return this;
        }


        public HallSpotDto build() {
            var hallSpotDto = new HallSpotDto();
            hallSpotDto.setSector(sector);
            return hallSpotDto;
        }
    }
}
