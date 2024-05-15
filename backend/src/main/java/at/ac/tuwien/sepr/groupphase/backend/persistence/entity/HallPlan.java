package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import java.sql.Blob;
import java.util.List;
import java.util.Objects;

@Entity
public class HallPlan extends AbstractEntity {

    @Lob
    @Column(name = "BACKGROUND_IMAGE")
    private Blob backgroundImage;

    @OneToMany
    private List<HallSector> sectors;

    public HallPlan(Blob backgroundImage, List<HallSector> sectors) {
        this.backgroundImage = backgroundImage;
        this.sectors = sectors;
    }

    public HallPlan() {
    }

    @Override
    public String toString() {
        return "HallPlan{" + "backgroundImage=" + backgroundImage + ", sectors=" + sectors + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallPlan hallPlan = (HallPlan) o;
        return Objects.equals(backgroundImage, hallPlan.backgroundImage) && Objects.equals(sectors, hallPlan.sectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundImage, sectors);
    }

    public Blob getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Blob backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<HallSector> getSectors() {
        return sectors;
    }

    public void setSectors(List<HallSector> sectors) {
        this.sectors = sectors;
    }
}
