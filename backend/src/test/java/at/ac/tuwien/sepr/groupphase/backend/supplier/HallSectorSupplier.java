package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import com.github.javafaker.Faker;
import java.util.List;

public class HallSectorSupplier {

    public static HallSector aSector(Faker faker){
        return new HallSector(HallPlanSupplier.aHallPlan(faker), faker.name().toString(), "{}", HallSpotSupplier.manySpots(faker));
    }

    public static HallSector emptySector(Faker faker){
        return new HallSector(null, faker.name().toString(), "{}", HallSpotSupplier.manySpots(faker));
    }

    public static List<HallSector> manySectors(Faker faker){
        return List.of(emptySector(faker), emptySector(faker));
    }
}
