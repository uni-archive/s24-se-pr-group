package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

public class HallplanSeatCreateRequest {
    private String frontendCoordinates;

    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }

    public void setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
    }

    @Override
    public String toString() {
        return "HallplanSpotCreateDto{" +
            "frontendCoordinates='" + frontendCoordinates + '\'' +
            '}';
    }
}