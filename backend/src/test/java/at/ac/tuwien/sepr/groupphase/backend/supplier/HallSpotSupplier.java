package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import com.github.javafaker.Faker;
import java.util.List;

public class HallSpotSupplier {

    public static HallSpot aSpot(Faker faker) {
        return new HallSpot(HallSectorSupplier.aSector(faker));
    }

    public static HallSeat aSeat(Faker faker) {
        return new HallSeat(HallSectorSupplier.aSector(faker), "{}");
    }

    public static HallSpot emptySpot(Faker faker) {
        return new HallSpot(null);
    }

    public static HallSpot emptySeat(Faker faker) {
        return new HallSeat(null, "{}");
    }

    public static List<HallSpot> manySpots(Faker faker) {
        return List.of(emptySpot(faker), emptySeat(faker), emptySpot(faker));
    }

}
