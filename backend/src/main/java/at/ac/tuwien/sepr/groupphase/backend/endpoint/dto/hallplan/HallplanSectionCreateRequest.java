package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class HallplanSectionCreateRequest {

    @NotNull(message = "Name must not be null")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Color must not be null")
    @Size(max = 10)
    private String color;

    @NotNull(message = "standingOnly must not be null")
    private boolean standingOnly;

    @NotNull(message = "Price must not be null")
    private int price;

    @NotNull(message = "frontendCoordinates must not be null")
    private String frontendCoordinates;

    @NotNull(message = "Spots must not be null")
    private List<HallplanSeatCreateRequest> spots;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isStandingOnly() {
        return standingOnly;
    }

    public void setStandingOnly(boolean standingOnly) {
        this.standingOnly = standingOnly;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }

    public void setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
    }

    public List<HallplanSeatCreateRequest> getSpots() {
        return spots;
    }

    public void setSpots(List<HallplanSeatCreateRequest> spots) {
        this.spots = spots;
    }

    @Override
    public String toString() {
        return "HallplanSectionCreateDto{" +
            "name='" + name + '\'' +
            ", color='" + color + '\'' +
            ", standingOnly=" + standingOnly +
            ", price=" + price +
            ", frontendCoordinates='" + frontendCoordinates + '\'' +
            ", spots=" + spots +
            '}';
    }
}