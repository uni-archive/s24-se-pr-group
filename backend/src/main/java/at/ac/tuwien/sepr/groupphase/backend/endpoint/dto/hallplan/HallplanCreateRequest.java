package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class HallplanCreateRequest {

    @NotNull(message = "Title must not be null")
    @Size(max = 100)
    private String name;

    @NotNull(message = "An image must be provided")
    private String backgroundImage;

    @NotNull(message = "sectors must not be null")
    private List<HallplanSectionCreateRequest> sectors;

    @Override
    public String toString() {
        return "HallplanCreateDto{" +
            "name='" + name + '\'' +
            ", backgroundImage=" + backgroundImage +
            ", sections=" + sectors +
            '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<HallplanSectionCreateRequest> getSectors() {
        return sectors;
    }

    public void setSectors(List<HallplanSectionCreateRequest> sections) {
        this.sectors = sections;
    }

    public Long getId() {
        return null;
    }
}