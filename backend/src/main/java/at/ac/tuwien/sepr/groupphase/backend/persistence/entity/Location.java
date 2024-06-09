package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Objects;

@Entity
public class Location extends AbstractEntity {

    @Column(name = "NAME")
    private String name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @OneToMany(mappedBy = "location", fetch = FetchType.EAGER)
    private List<Show> shows;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HALLPLAN_ID")
    private HallPlan hallPlan;


    public Location(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public Location() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public Location setAddress(Address address) {
        this.address = address;
        return this;
    }

    public List<Show> getShows() {
        return shows;
    }

    public Location setShows(List<Show> shows) {
        this.shows = shows;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public HallPlan getHallPlan() {
        return hallPlan;
    }

    public Location setHallPlan(HallPlan hallPlan) {
        this.hallPlan = hallPlan;
        return this;
    }

    @Override
    public String toString() {
        return "Location{" + "name='" + name + '\'' + '}';
    }
}
