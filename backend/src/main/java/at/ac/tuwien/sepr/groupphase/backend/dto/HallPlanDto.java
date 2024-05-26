package at.ac.tuwien.sepr.groupphase.backend.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class HallPlanDto implements AbstractDto {
    private Long id;

    @NotNull(message = "Title must not be null")
    @Size(max = 100)
    private String name;

    @NotNull(message = "An image must be provided")
    private String backgroundImage;

    @JsonManagedReference
    @NotNull(message = "Sectors must not be null")
    private List<HallSectorDto> sectors;

    public HallPlanDto withoutSectors() {
        return new HallPlanDto(id, name, backgroundImage, null);
    }

    public HallPlanDto(Long id, String name, String backgroundImage, List<HallSectorDto> sectors) {
        this.id = id;
        this.name = name;
        this.backgroundImage = backgroundImage;
        this.sectors = sectors;
    }

    @Override
    public String toString() {
        return "HallplanCreateDto{" +
            "name='" + name + '\'' +
            ", backgroundImage=" + backgroundImage +
            ", sectors=" + sectors +
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

    public List<HallSectorDto> getSectors() {
        return sectors;
    }

    public void setSectors(List<HallSectorDto> sectors) {
        this.sectors = sectors;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}