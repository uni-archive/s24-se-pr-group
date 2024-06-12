package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.aUserEntity;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataGeneratorUtils {

    private static final Random rng = new Random();
    private static final Faker faker = new Faker();

    public static <T> List<T> fakeMany(Supplier<T> generator, int count) {
        return IntStream.range(0, count).mapToObj(i -> generator.get()).collect(Collectors.toList());
    }

    public static <T> List<T> fakeMany(Function<Integer, T> generator, int count) {
        return IntStream.range(0, count).mapToObj(generator::apply).toList();
    }

    public static <T> List<T> getRandomSubset(List<T> choices) {
        var subsetSize = rng.nextInt(0, choices.size());

        List<T> copiedList = new ArrayList<>(choices);

        if (subsetSize > copiedList.size()) {
            throw new IllegalArgumentException("Requested subset size cannot exceed the list size.");
        }

        List<T> subset = new ArrayList<>();

        for (int i = 0; i < subsetSize; i++) {
            int randomIndex = rng.nextInt(copiedList.size());
            subset.add(copiedList.get(randomIndex));
            copiedList.remove(randomIndex);
        }

        return subset;
    }

    @SafeVarargs
    public static <T> Supplier<T> fakeOneOf(Supplier<T>... generators) {
        var idx = rng.nextInt(generators.length);
        return () -> generators[idx].get();
    }

    public static <T> T randomChoice(List<T> choices) {
        if (choices.size() > 0) {
            var idx = rng.nextInt(choices.size());
            return choices.get(idx);
        }
        return null;
    }

    public static <T> T randomChoice(T[] choices) {
        var idx = rng.nextInt(choices.length);
        return choices[idx];
    }

    public static <T> List<T> randomChoices(List<T> choices) {
        return getRandomSubset(choices);
    }

    public static LocalDateTime randomDateInRange(int maxPastDays, int maxFutureDays) {
        var date = faker.date().between(
            faker.date().past(maxPastDays, TimeUnit.DAYS),
            faker.date().future(maxFutureDays, TimeUnit.DAYS)
        );
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    public static Address fakeAddress() {
        var fakedAddr = faker.address();
        return new Address(
            fakedAddr.streetAddress(),
            fakedAddr.zipCode(),
            fakedAddr.city(),
            fakedAddr.country()
        );
    }

    public static ApplicationUser fakeUser(String mail, PasswordEncoder encoder, Address address) {
        var usr = aUserEntity(mail, encoder, faker);
        usr.setAddress(address);
        return usr;
    }

    public static List<Artist> fakeManyArtists(int count) {
        return fakeMany(() -> {
            var artist = new Artist();
            artist.setArtistName(faker.artist().name());
            artist.setFirstName(faker.name().firstName());
            artist.setLastName(faker.name().lastName());
            artist.setShows(new ArrayList<>());
            return artist;
        }, count);
    }

    public static List<Event> fakeManyEvents(int count) {
        return fakeMany(() -> {
            var event = new Event();
            event.setEventType(oneOf(EventType.class));
            event.setTitle(faker.book().title());
            event.setDescription(faker.weather().description());
            event.setDuration((long) ((Math.random() + 60) * 60));
            return event;
        }, count);
    }

    public static List<Show> fakeManyShows(List<Event> events, List<Artist> artists, int count) {
        var shows = fakeMany(() -> {
            var show = new Show();
            show.setEvent(randomChoice(events));
            show.setArtists(randomChoices(artists));
            show.setDateTime(randomDateInRange(365, 365));
            return show;
        }, count);

        shows.forEach(s -> s.getArtists().forEach(a -> a.getShows().add(s)));

        shows.stream().collect(Collectors.toMap(
                Show::getEvent,
                s -> new ArrayList<Show>() {{
                    add(s);
                }},
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
            ))
            .forEach(Event::setShows);

        return shows;
    }

    public static List<HallSector> fakeManyHallSectors(HallPlan hallPlan, int count) {
        return fakeMany(() -> {
            var sector = new HallSector();
            sector.setName(faker.name().firstName());
            sector.setHallPlan(hallPlan);
            sector.setFrontendCoordinates("");
            return sector;
        }, count);
    }

    public static List<HallSpot> fakeManyHallSpots(List<HallSector> sectors, int count) {
        return fakeMany(fakeOneOf(
            () -> {
                var seat = new HallSeat();
                seat.setSector(randomChoice(sectors));
                seat.setFrontendCoordinates("");
                return seat;
            },
            () -> {
                var spot = new HallSpot();
                spot.setSector(randomChoice(sectors));
                return spot;
            }), count);
    }

    public static List<HallSectorShow> fakeManyHallSectorShows(
        List<Show> shows,
        List<HallSector> sectors,
        ConcurrentHashMap<HallSector, List<Show>> sectorShowsChosenMap,
        int count
    ) {
        // we need to guarantee that no duplicate (show, sector) tuple exists
        var sectorShowsChoiceMap = shows.stream().collect(Collectors.toConcurrentMap(
            s -> s,
            s -> new CopyOnWriteArrayList<HallSector>() {{
                addAll(sectors);
            }}
        ));

        sectorShowsChosenMap.clear();

        // creating sector-shows
        return fakeMany(() -> {
            var sectorShow = new HallSectorShow();
            var show = randomChoice(shows);
            var sector = randomChoice(sectorShowsChoiceMap.get(show));
            sectorShowsChoiceMap.get(show).remove(sector);

            sectorShow.setShow(show);
            sectorShow.setSector(sector);

            if (Objects.nonNull(sector)) {
                sectorShowsChosenMap.putIfAbsent(sector, new ArrayList<>());
                sectorShowsChosenMap.get(sector).add(show);
            }
            sectorShow.setPrice(faker.random().nextInt(100, 500 * 100));
            return sectorShow;
        }, count);
    }

    public static List<Ticket> fakeManyTickets(List<HallSpot> hallSpots,
        ConcurrentHashMap<HallSector, List<Show>> sectorShowsChosenMap, int count) {
        return fakeMany(() -> {
            var ticket = new Ticket();
            ticket.setHash(faker.random().hex());
            ticket.setReserved(faker.random().nextBoolean());
            ticket.setValid(faker.random().nextBoolean());

            var validHallSpots = hallSpots.stream()
                .filter(hs -> sectorShowsChosenMap.containsKey(hs.getSector()))
                .collect(Collectors.toList());

            var hallSpot = randomChoice(validHallSpots);

            var show = randomChoice(sectorShowsChosenMap.get(hallSpot.getSector()));

            ticket.setShow(show);
            ticket.setHallSpot(hallSpot);
            return ticket;
        }, count);
    }

    public static List<Order> fakeManyOrders(List<Ticket> tickets, List<ApplicationUser> users, int count) {
        return fakeMany(() -> {
            var order = new Order();
            order.setCustomer(randomChoice(users));
            order.setTickets(randomChoices(tickets));
            order.getTickets().forEach(t -> t.setOrder(order));
            return order;
        }, count);
    }

    public static <T extends Enum<T>> T oneOf(Class<T> enu) {
        return randomChoice(enu.getEnumConstants());
    }
}
