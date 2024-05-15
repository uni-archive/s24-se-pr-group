package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class HallSpot extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "SECTOR_ID")
    private HallSector sector;

    public HallSpot() {
    }

    public HallSpot(HallSector sector) {
        this.sector = sector;
    }

    public HallSector getSector() {
        return sector;
    }

    public void setSector(HallSector sector) {
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
        HallSpot hallSpot = (HallSpot) o;
        return Objects.equals(sector, hallSpot.sector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sector);
    }

    @Override
    public String toString() {
        return "HallSpot{" + "sector=" + sector + '}';
    }
}
