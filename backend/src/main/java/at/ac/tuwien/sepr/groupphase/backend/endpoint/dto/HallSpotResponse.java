package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

// NOTE: This must not be a record since HallSeatResponse inherits from this class and records are final classes.

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = HallSeatResponse.class, name = "hallSeatResponse")
})
@Schema(description = "Generic Hall-Spot")
public class HallSpotResponse {
    private Long id;
    private HallSectorResponse sector;

    public HallSpotResponse() {}

    public HallSpotResponse(Long id, HallSectorResponse sector) {
        this.id = id;
        this.sector = sector;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HallSectorResponse getSector() {
        return sector;
    }

    public void setSector(HallSectorResponse sector) {
        this.sector = sector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallSpotResponse that = (HallSpotResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(sector, that.sector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sector);
    }

    @Override
    public String toString() {
        return "HallSpotResponse{" + "id=" + id + ", sector=" + sector + '}';
    }
}
