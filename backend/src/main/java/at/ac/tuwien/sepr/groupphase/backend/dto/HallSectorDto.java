package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

public class HallSectorDto implements AbstractDto {
    private Long id;

    @JsonBackReference
    private HallPlanDto hallPlan;
    private String name;
    private String frontendCoordinates;
    @JsonManagedReference
    private List<HallSpotDto> seats;

    private String color;

    private HallSectorShowDto hallSectorShow;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public Long getId() {
        return id;
    }



    public HallSectorShowDto getHallSectorShow() {
        return hallSectorShow;
    }


    public HallPlanDto getHallPlan() {
        return hallPlan;
    }


    public String getName() {
        return name;
    }


    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }


    public List<HallSpotDto> getSeats() {
        return seats;
    }

    public HallSectorDto setId(Long id) {
        this.id = id;
        return this;
    }

    public HallSectorDto setHallPlan(HallPlanDto hallPlan) {
        this.hallPlan = hallPlan;
        return this;
    }

    public HallSectorDto setName(String name) {
        this.name = name;
        return this;
    }

    public HallSectorDto setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
        return this;
    }

    public HallSectorDto setSeats(List<HallSpotDto> seats) {
        this.seats = seats;
        return this;
    }

    public HallSectorDto setHallSectorShow(HallSectorShowDto hallSectorShow) {
        this.hallSectorShow = hallSectorShow;
        return this;
    }

    public HallSectorDto(Long id, HallPlanDto hallPlan, String name, String frontendCoordinates, List<HallSpotDto> seats, HallSectorShowDto hallSectorShow) {
        this.id = id;
        this.hallPlan = hallPlan;
        this.name = name;
        this.frontendCoordinates = frontendCoordinates;
        this.seats = seats;
        this.hallSectorShow = hallSectorShow;
    }

    public HallSectorDto() {
    }

    public HallSectorDto withoutSeats() {
        var hallSector = new HallSectorDto(id, hallPlan, name, frontendCoordinates, null, hallSectorShow);
        hallSector.setColor(color);
        return hallSector;
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
