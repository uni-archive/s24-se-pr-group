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

    public HallSectorShowDto setId(Long id) {
        this.id = id;
        return this;
    }

    public HallSectorShowDto setShow(ShowDto show) {
        this.show = show;
        return this;
    }

    public HallSectorShowDto setSector(HallSectorDto sector) {
        this.sector = sector;
        return this;
    }

    public HallSectorShowDto setPrice(long price) {
        this.price = price;
        return this;
    }

    public ShowDto getShow() {
        return show;
    }

    public HallSectorDto getSector() {
        return sector;
    }

    public long getPrice() {
        return price;
    }

    public HallSectorShowDto() {
    }

    public HallSectorShowDto(Long id, ShowDto show, HallSectorDto sector, long price) {
        this.id = id;
        this.show = show;
        this.sector = sector;
        this.price = price;
    }
}
