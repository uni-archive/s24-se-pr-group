package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddressCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;

public class AddressSupplier {

    public static AddressCreateRequest addressCreateRequest() {
        AddressCreateRequest addressCreateRequest = new AddressCreateRequest(
            "Ringstraße 10/4A", "Wien", "1010", "Austria");
        return addressCreateRequest;
    }

    public static AddressDto anAddress() {
        AddressDto addressDto = new AddressDto()
            .setStreet("Ringstraße 10/4A")
            .setCity("Wien")
            .setZip("1010")
            .setCountry("Austria");
        return addressDto;
    }

    public static Address anAddressEntity(){
        Address address = new Address();
        address.setStreet("Ringstraße 10/4A");
        address.setCity("Wien");
        address.setZip("1010");
        address.setCountry("Austria");
        return address;
    }

}
