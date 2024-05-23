package at.ac.tuwien.sepr.groupphase.backend.dto;


import org.springframework.data.domain.Pageable;

public class LocationSearch {

    private String name;
    private AddressSearch addressSearch;
    private Pageable pageable;

    public LocationSearch(String name, AddressSearch addressSearch, Pageable pageable) {
        this.name = name;
        this.addressSearch = addressSearch;
        this.pageable = pageable;
    }

    public String getName() {
        return name;
    }

    public LocationSearch setName(String name) {
        this.name = name;
        return this;
    }

    public AddressSearch getAddressSearch() {
        return addressSearch;
    }

    public LocationSearch setAddressSearch(AddressSearch addressSearch) {
        this.addressSearch = addressSearch;
        return this;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public LocationSearch setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
