package at.ac.tuwien.sepr.groupphase.backend.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class HallSpotDto implements AbstractDto {
    private Long id;

    @JsonBackReference
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
