package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class LocationSearchRequest {

    private String name;
    private AddressSearchRequest addressSearch;
    private boolean withUpComingShows;
    private PageRequest pageRequest;

    public LocationSearchRequest(String name, AddressSearchRequest addressSearch, PageRequest pageRequest) {
        this.name = name;
        this.addressSearch = addressSearch;
        this.pageRequest = pageRequest;
    }

    public String getName() {
        return name;
    }

    public LocationSearchRequest setName(String name) {
        this.name = name;
        return this;
    }

    public AddressSearchRequest getAddressSearch() {
        return addressSearch;
    }

    public LocationSearchRequest setAddressSearch(AddressSearchRequest addressSearch) {
        this.addressSearch = addressSearch;
        return this;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public LocationSearchRequest setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public boolean isWithUpComingShows() {
        return withUpComingShows;
    }

    public LocationSearchRequest setWithUpComingShows(boolean withUpComingShows) {
        this.withUpComingShows = withUpComingShows;
        return this;
    }
}
