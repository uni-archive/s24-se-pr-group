package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class HallSeat extends HallSpot {
    @Column(name = "FRONTEND_COORDINATES")
    private String frontendCoordinates;

    public String getFrontendCoordinates() {
        return frontendCoordinates;
    }

    public void setFrontendCoordinates(String frontendCoordinates) {
        this.frontendCoordinates = frontendCoordinates;
    }

    public HallSeat() {
    }

    public HallSeat(HallSector sector, String frontendCoordinates) {
        super(sector);
        this.frontendCoordinates = frontendCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HallSeat hallSeat = (HallSeat) o;
        return Objects.equals(frontendCoordinates, hallSeat.frontendCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), frontendCoordinates);
    }
}
