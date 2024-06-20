package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Profile("generateData")
@Component
public class HallPlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(HallPlanGenerator.class);

    @Autowired
    private HallPlanGeneratorUtil hallPlanGeneratorUtil;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    private static final int MAX_SECTORS = 5; // Number of sectors to generate

    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        hallPlanGeneratorUtil.generateRandomHallPlans(dataGenerationConfig.hallPlanAmount); // Generate 300 random hall plans
    }

}
