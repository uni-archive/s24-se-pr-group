package at.ac.tuwien.sepr.groupphase.backend.supplier;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShowSupplier {
    public static Show aShow(Faker faker) {
        var show = new Show();
        show.setDateTime(randomDateInRange(faker, 365, 365));
        return show;
    }

    public static List<Show> manyShows(Faker faker, int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> aShow(faker))
            .collect(Collectors.toList());
    }

    private static LocalDateTime randomDateInRange(Faker faker, int maxPastDays, int maxFutureDays) {
        var date = faker.date().between(
            faker.date().past(maxPastDays, TimeUnit.DAYS),
            faker.date().future(maxFutureDays, TimeUnit.DAYS)
        );
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
