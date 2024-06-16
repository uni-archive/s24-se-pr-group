package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;

public class HallSectorShowCreationDto {
    private HallSectorDto sectorDto;
    private long price;

    public HallSectorShowCreationDto(HallSectorDto sectorDto, long price) {
        this.sectorDto = sectorDto;
        this.price = price;
    }

    public HallSectorDto getSectorDto() {
        return sectorDto;
    }

    public long getPrice() {
        return price;
    }

    public HallSectorShowCreationDto setSectorDto(HallSectorDto sectorDto) {
        this.sectorDto = sectorDto;
        return this;
    }

    public HallSectorShowCreationDto setPrice(long price) {
        this.price = price;
        return this;
    }

    public HallSectorShowCreationDto() {
    }
}
