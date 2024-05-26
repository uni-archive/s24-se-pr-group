package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LocationValidator extends AbstractValidator<LocationDto> {

    private final AddressService addressService;

    public LocationValidator(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public void validateForUpdate(LocationDto object) throws ValidationException {
        super.validateForUpdate(object);
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(object.getName())) {
            errors.add("Name is required");
        }
        if (object.getAddress() == null || object.getAddress().getId() == null) {
            errors.add("Address is required");
        } else {
            try {
                if (addressService.findById(object.getAddress().getId()) == null) {
                    errors.add("Address does not exist");
                }
            } catch (EntityNotFoundException e) {
                errors.add("Address does not exist");
            }
        }
        endValidation(errors);
    }

    @Override
    public void validateForCreate(LocationDto object) throws ValidationException {
        super.validateForCreate(object);
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(object.getName())) {
            errors.add("Name is required");
        }
        if (object.getAddress() == null) {
            errors.add("Address is required");
        }
        endValidation(errors);
    }
}
