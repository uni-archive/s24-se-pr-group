package at.ac.tuwien.sepr.groupphase.backend.dto;


import org.springframework.data.domain.Pageable;

public class LocationSearch {

    private String name;
    private AddressSearch addressSearch;
    private boolean withUpComingShows;
    private Pageable pageable;

    public LocationSearch(String name, AddressSearch addressSearch, boolean withUpComingShows, Pageable pageable) {
        this.name = name;
        this.addressSearch = addressSearch;
        this.pageable = pageable;
        this.withUpComingShows = withUpComingShows;
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

    public boolean isWithUpComingShows() {
        return withUpComingShows;
    }

    public LocationSearch setWithUpComingShows(boolean withUpComingShows) {
        this.withUpComingShows = withUpComingShows;
        return this;
    }
}
