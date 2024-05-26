package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.sql.Blob;
import java.util.List;
import java.util.Objects;

@Entity
public class HallPlan extends AbstractEntity {

    @Column(name = "NAME")
    private String name;

    @Column(name = "BACKGROUND_IMAGE", columnDefinition = "TEXT")
    private String backgroundImage;

    @OneToMany(mappedBy = "hallPlan", cascade = CascadeType.ALL)
    private List<HallSector> sectors;

    public HallPlan(String backgroundImage, List<HallSector> sectors) {
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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HallSector> getSectors() {
        return sectors;
    }

    public void setSectors(List<HallSector> sectors) {
        this.sectors = sectors;
    }
}
