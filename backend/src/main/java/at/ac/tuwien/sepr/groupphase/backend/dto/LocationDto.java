package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.util.Objects;

public class LocationDto implements AbstractDto {

    private Long id;
    private String name;
    private AddressDto address;
    private HallPlanDto hallPlan;


    public LocationDto() {
    }

    public LocationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public LocationDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public LocationDto setName(String name) {
        this.name = name;
        return this;
    }

    public AddressDto getAddress() {
        return address;
    }

    public LocationDto setAddress(AddressDto address) {
        this.address = address;
        return this;
    }

    public HallPlanDto getHallPlan() {
        return hallPlan;
    }

    public void setHallPlan(HallPlanDto hallPlan) {
        this.hallPlan = hallPlan;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationDto that = (LocationDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(
            getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getAddress());
    }

    @Override
    public String toString() {
        return "LocationDto{" + "id=" + id + ", name='" + name + '\'' + ", addressDto=" + address + '}';
    }
}
