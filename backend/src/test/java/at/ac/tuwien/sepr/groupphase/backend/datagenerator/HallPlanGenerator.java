package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(1)
@Profile("generateData")
@Component
public class HallPlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(HallPlanGenerator.class);

    @Autowired
    private HallPlanGeneratorUtil hallPlanGeneratorUtil;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private HallSectorRepository hallSectorRepository;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    private static final int MAX_SECTORS = 5; // Number of sectors to generate

    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        genOpenAirHallplan();
        hallPlanGeneratorUtil.generateRandomHallPlans(dataGenerationConfig.hallPlanAmount); // Generate 300 random hall plans
    }

    private void genOpenAirHallplan() throws ForbiddenException, ValidationException {
        var hallPlan = new HallPlan(
            hallPlanGeneratorUtil.loadImage("backend/src/test/resources/datagen/hallplan/openair.txt"),
            "Open Air",
            List.of()
        );
        hallPlanRepository.saveAndFlush(hallPlan);

        //  SELECT concat('new HallSector(hallPlan, "', name, '", "', replace(frontend_coordinates, '"', '\"'), '", List.of())') FROM HALL_SECTOR where hall_plan_id = 3;

        var sector1 = new HallSector(hallPlan, "Sitzbereich", "[{\"x\":68.27,\"y\":171.14},{\"x\":58.61,\"y\":391.76},{\"x\":264.73,\"y\":383.7},{\"x\":290.49,\"y\":243.61}]", List.of());
        sector1.setColor("#FF0000");
        hallSectorRepository.saveAndFlush(sector1);
        var sector2 = new HallSector(hallPlan, "Stehbereich", "[{\"x\":317.87,\"y\":277.42},{\"x\":332.36,\"y\":469.05},{\"x\":474.07,\"y\":612.37},{\"x\":625.44,\"y\":440.06},{\"x\":633.49,\"y\":280.64}]", List.of());
        sector2.setColor("#00FF00");
        hallSectorRepository.saveAndFlush(sector2);


        // SELECT concat('new HallSeat(sector1, "', replace(frontend_coordinates, '"', '\"'), '"),') FROM HALL_SEAT s left join hall_spot p on s.id = p.id where sector_id = 23
        var sector1seats = List.of(
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":68.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":181.14869638341636}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":196.14869638341636}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":83.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":196.14869638341636}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":98.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":196.14869638341636}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":113.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":196.14869638341636}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":128.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":143.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":158.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":211.14869638341636}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":173.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":188.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":203.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":226.14869638341636}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":218.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":233.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":361.14869638341634}"),
            new HallSeat(sector1, "{\"x\":248.61512704747886,\"y\":376.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":241.14869638341636}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":271.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":286.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":301.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":316.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":331.14869638341634}"),
            new HallSeat(sector1, "{\"x\":263.61512704747884,\"y\":346.14869638341634}"),
            new HallSeat(sector1, "{\"x\":278.61512704747884,\"y\":256.14869638341634}"),
            new HallSeat(sector1, "{\"x\":278.61512704747884,\"y\":271.14869638341634}")
        );
        hallSpotRepository.saveAllAndFlush(sector1seats);

        for (int i = 0; i <= 400; i++) {
            var spot = new HallSpot(sector2);
            hallSpotRepository.saveAndFlush(spot);
        }
    }

}
