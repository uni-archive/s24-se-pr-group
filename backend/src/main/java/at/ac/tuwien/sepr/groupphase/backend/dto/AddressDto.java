package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.util.Objects;

public class AddressDto implements AbstractDto {

    private Long id;
    private String street;
    private String city;
    private String zip;
    private String country;

    public AddressDto(Long id, String street, String city, String zip, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }

    public AddressDto() {
    }

    @Override
    public String toString() {
        return "AddressDto{" + "id=" + id + ", street='" + street + '\'' + ", city='" + city + '\'' + ", zip='"
            + zip + '\'' + ", country='" + country + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddressDto that = (AddressDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getStreet(), that.getStreet()) && Objects.equals(
            getCity(), that.getCity()) && Objects.equals(getZip(), that.getZip()) && Objects.equals(
            getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStreet(), getCity(), getZip(), getCountry());
    }

    @Override
    public Long getId() {
        return id;
    }

    public AddressDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AddressDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public AddressDto setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public AddressDto setCountry(String country) {
        this.country = country;
        return this;
    }
}
