package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.util.Objects;

public class HallSeatDto extends HallSpotDto {
    private String frontendCoordinates;

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
        if (!(o instanceof HallSeatDto that)) {
            return false;
        }
        return Objects.equals(frontendCoordinates, that.frontendCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frontendCoordinates);
    }

    @Override
    public String toString() {
        return "HallSeatDto{" + "frontendCoordinates='" + frontendCoordinates + '\'' + "} " + super.toString();
    }
}
