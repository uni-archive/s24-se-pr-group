package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.AddressDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressValidator extends AbstractValidator<AddressDto> {

    private final AddressDao addressDao;

    public AddressValidator(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    public List<String> validateAddress(AddressDto addressDto) {
        List<String> errors = new ArrayList<>();

        if (addressDto.getStreet() == null || addressDto.getStreet().isEmpty()) {
            errors.add("Straße darf nicht leer sein.");
        } else if (addressDto.getStreet().length() > 255) {
            errors.add("Straße darf nicht länger als 255 Zeichen sein.");
        }
        if (addressDto.getCity() == null || addressDto.getCity().isEmpty()) {
            errors.add("Stadt darf nicht leer sein.");
        } else if (addressDto.getCity().length() > 255) {
            errors.add("Stadt darf nicht länger als 255 Zeichen sein.");
        }
        if (addressDto.getZip() == null || addressDto.getZip().isEmpty()) {
            errors.add("Postleitzahl darf nicht leer sein.");
        } else {
            try {
                Integer.parseInt(addressDto.getZip());
            } catch (NumberFormatException e) {
                errors.add("Postleitzahl muss eine Zahl sein.");
            }
        }
        return errors;
    }

    @Override
    public void validateForUpdate(AddressDto addressDto) throws ValidationException {
        super.validateForUpdate(addressDto);
        List<String> errors = validateAddress(addressDto);
        try {
            addressDao.findById(addressDto.getId());

        } catch (EntityNotFoundException e) {
            errors.add("Address with id " + addressDto.getId() + " does not exist");
        }
        endValidation(errors);
    }

    @Override
    public void validateForCreate(AddressDto addressDto) throws ValidationException {
        super.validateForCreate(addressDto);
        List<String> errors = validateAddress(addressDto);
        endValidation(errors);
    }
}
