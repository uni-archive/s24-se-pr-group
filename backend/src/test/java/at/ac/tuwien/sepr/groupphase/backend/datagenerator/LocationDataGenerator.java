package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.dto.AddressDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.LocationDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Profile("generateData")
@Component
public class LocationDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(LocationDataGenerator.class);
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;



    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        Faker faker = new Faker();
        Random random = new Random();
        List<HallPlan> all = hallPlanRepository.findHallPlansByNameContainingIgnoreCase("open air", Pageable.unpaged());
        for (int i = 1; i <= dataGenerationConfig.locationAmount; i++) {
            log.info("Generating location " + i + "/" + dataGenerationConfig.locationAmount);
            Address address = new Address();
            address.setStreet(faker.address().streetAddress());
            address.setZip(String.valueOf(random.nextInt(99999)));
            address.setCity(faker.address().city());
            address.setCountry(faker.address().country());
            addressRepository.saveAndFlush(address);

            Location location = new Location();
            location.setName(faker.company().name());
            location.setAddress(address);
            location.setHallPlan(all.get(random.nextInt(all.size())));
            locationRepository.save(location);
        }
    }
}

