package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Hall-Seat")
public class HallSeatResponse extends HallSpotResponse {

    private String frontendCoordinates;

    public HallSeatResponse() {}

    public HallSeatResponse(Long id, HallSectorResponse sector) {
        super(id, sector);
    }

    public HallSeatResponse(Long id, HallSectorResponse sector, String frontendCoordinates) {
        super(id, sector);
        this.frontendCoordinates = frontendCoordinates;
    }

    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }

    public void setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        HallSeatResponse that = (HallSeatResponse) o;
        return Objects.equals(frontendCoordinates, that.frontendCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), frontendCoordinates);
    }

    @Override
    public String toString() {
        return "HallSeatResponse{" + "frontendCoordinates='" + frontendCoordinates + '\'' + "} " + super.toString();
    }
}
