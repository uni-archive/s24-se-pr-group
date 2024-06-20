package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.service.AddressService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallPlanServiceImpl;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationValidator extends AbstractValidator<LocationDto> {

    private final AddressService addressService;
    private final HallPlanServiceImpl hallPlanServiceImpl;

    public LocationValidator(AddressService addressService, HallPlanServiceImpl hallPlanServiceImpl) {
        this.addressService = addressService;
        this.hallPlanServiceImpl = hallPlanServiceImpl;
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
            } catch (DtoNotFoundException e) {
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
        if (object.getHallPlan() == null || object.getHallPlan().getId() == null) {
            errors.add("Hall plan is required");
        } else {
            try {
                hallPlanServiceImpl.findById(object.getHallPlan().getId());
            } catch (DtoNotFoundException e) {
                errors.add("Hall plan does not exist");
            }
        }
        endValidation(errors);
    }
}
