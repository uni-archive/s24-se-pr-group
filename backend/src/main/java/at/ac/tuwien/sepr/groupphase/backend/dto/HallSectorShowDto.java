package at.ac.tuwien.sepr.groupphase.backend.dto;

public class HallSectorShowDto implements AbstractDto {
    private Long id;
    private ShowDto show;
    private HallSectorDto sector;
    private long price;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShowDto getShow() {
        return show;
    }

    public void setShow(ShowDto show) {
        this.show = show;
    }

    public HallSectorDto getSector() {
        return sector;
    }

    public void setSector(HallSectorDto sector) {
        this.sector = sector;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public class HallSectorShowDtoBuilder {
        private ShowDto show;
        private HallSectorDto sector;
        private long price;

        public HallSectorShowDtoBuilder withShow(ShowDto show) {
            this.show = show;
            return this;
        }

        public HallSectorShowDtoBuilder withSector(HallSectorDto sector) {
            this.sector = sector;
            return this;
        }

        public HallSectorShowDtoBuilder withPrice(long price) {
            this.price = price;
            return this;
        }

        public HallSectorShowDto createHallSectorShowDto() {
            var hallSectorShowDto = new HallSectorShowDto();
            hallSectorShowDto.setId(id);
            hallSectorShowDto.setShow(show);
            hallSectorShowDto.setSector(sector);
            hallSectorShowDto.setPrice(price);
            return hallSectorShowDto;
        }
    }
}
