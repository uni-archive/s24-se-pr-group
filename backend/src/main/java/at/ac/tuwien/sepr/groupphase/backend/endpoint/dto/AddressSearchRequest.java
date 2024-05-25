package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class AddressSearchRequest {

    private String city;
    private String street;
    private String postalCode;
    private String country;

    public AddressSearchRequest(String city, String street, String postalCode, String country) {
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public AddressSearchRequest setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressSearchRequest setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public AddressSearchRequest setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public AddressSearchRequest setCountry(String country) {
        this.country = country;
        return this;
    }
}
