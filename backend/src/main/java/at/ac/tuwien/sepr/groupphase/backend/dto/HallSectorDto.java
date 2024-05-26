package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class HallSectorDto implements AbstractDto {
    private Long id;

    @JsonIgnore
    private HallPlanDto hallPlan;
    private String name;
    private String frontendCoordinates;
    private List<HallSpotDto> seats;

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

    public HallPlanDto getHallPlan() {
        return hallPlan;
    }

    public void setHallPlan(HallPlanDto hallPlan) {
        this.hallPlan = hallPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }

    public void setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
    }

    public List<HallSpotDto> getSeats() {
        return seats;
    }

    public void setSeats(List<HallSpotDto> seats) {
        this.seats = seats;
    }

    public HallSectorDto(Long id, HallPlanDto hallPlan, String name, String frontendCoordinates, List<HallSpotDto> seats, HallSectorShowDto hallSectorShow) {
        this.id = id;
        this.hallPlan = hallPlan;
        this.name = name;
        this.frontendCoordinates = frontendCoordinates;
        this.seats = seats;
        this.hallSectorShow = hallSectorShow;
    }

    public HallSectorDto withoutSeats() {
        return new HallSectorDto(id, hallPlan, name, frontendCoordinates, null, hallSectorShow);
    }

    public class HallSectorDtoBuilder {
        private HallSectorShowDto hallSectorShow;

        public HallSectorDtoBuilder withHallSectorShow(HallSectorShowDto hallSectorShow) {
            this.hallSectorShow = hallSectorShow;
            return this;
        }

        public HallSectorDto build() {
            var hallSector = new HallSectorDto(null, null, null, null, null, null);
            hallSector.setHallSectorShow(hallSectorShow);
            return hallSector;
        }
    }
}
