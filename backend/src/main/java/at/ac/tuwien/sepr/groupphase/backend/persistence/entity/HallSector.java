package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;

@Entity
public class HallSector extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "HALL_PLAN_ID")
    private HallPlan hallPlan;

    @Column(name = "NAME")
    private String name;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "FRONTEND_COORDINATES", length = 8096)
    private String frontendCoordinates;

    @OneToMany(mappedBy = "sector")
    private List<HallSpot> seats;

    @Override
    public String toString() {
        return "HallSector{" + "hallPlan=" + hallPlan + ", name='" + name + '\'' + ", frontendCoordinates='" + frontendCoordinates + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallSector that = (HallSector) o;
        return Objects.equals(hallPlan, that.hallPlan) && Objects.equals(name, that.name) && Objects.equals(frontendCoordinates, that.frontendCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), hallPlan, name, frontendCoordinates);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public HallPlan getHallPlan() {
        return hallPlan;
    }

    public void setHallPlan(HallPlan hallPlan) {
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

    public List<HallSpot> getSeats() {
        return seats;
    }

    public void setSeats(List<HallSpot> seats) {
        this.seats = seats;
    }

    public HallSector() {
    }

    public HallSector(HallPlan hallPlan, String name, String frontendCoordinates, List<HallSpot> seats) {
        this.hallPlan = hallPlan;
        this.name = name;
        this.frontendCoordinates = frontendCoordinates;
        this.seats = seats;
        this.color = "#ff0000";
    }
}
