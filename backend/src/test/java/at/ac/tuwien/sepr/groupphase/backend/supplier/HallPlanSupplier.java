package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import com.github.javafaker.Faker;

public class HallPlanSupplier {

    public static HallPlan aHallPlan(Faker faker) {
        return new HallPlan(faker.rickAndMorty().location(), null, HallSectorSupplier.manySectors(faker));
    }

}
