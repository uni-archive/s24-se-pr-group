package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("generateData")
@Component
public class LocationDataGenerator {


    @Autowired
    private LocationService locationService;


    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        Faker faker = new Faker();
        Random random = new Random();
        for (int i = 1; i <= 40; i++) {
            LocationDto locationDto = new LocationDto();
            locationDto.setName(faker.company().name());
            locationDto.setAddress(new AddressDto()
                .setStreet(faker.address().streetAddress())
                .setZip(String.valueOf(random.nextInt(99999)))
                .setCity(faker.address().city())
                .setCountry(faker.address().country()));
            locationService.create(locationDto);
        }
    }
}

