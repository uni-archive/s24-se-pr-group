package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.EventType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSectorShow;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("generateData")
@Component
public class TicketViewDataGenerator {

    private static final int COUNT_FACTOR = 1;
    private static final int EVENTS_COUNT = 10 * COUNT_FACTOR;
    private static final int ARTISTS_COUNT = 100 * COUNT_FACTOR;
    private static final int SHOWS_COUNT = 100 * COUNT_FACTOR;
    private static final int SECTORS_COUNT = 20 * COUNT_FACTOR;
    private static final int HALLSPOTS_COUNT = 500 * COUNT_FACTOR;
    private static final int SECTORS_SHOWS_COUNT = 100 * COUNT_FACTOR;
    private static final int TICKETS_COUNT = 100 * COUNT_FACTOR;
    private static final Random rng = new Random();
    private static final Faker faker = new Faker();
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private HallSpotRepository hallSpotRepository;
    @Autowired
    private HallSectorRepository hallSectorRepository;
    @Autowired
    private HallSectorShowRepository hallSectorShowRepository;
    @Autowired
    private HallPlanRepository hallPlanRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void generateData() {
        // creating user for order
        var customer = new ApplicationUser("ticketview-user-48@email.com", "", "tview", "tview", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false, UserType.CUSTOMER, false);
        customer.setPassword(passwordEncoder.encode("password" + customer.getSalt()));

        userRepository.save(customer);

        // creating artists
        var artists = fakeMany(() -> {
            var artist = new Artist();
            artist.setArtistName(faker.artist().name());
            artist.setFirstName(faker.name().firstName());
            artist.setLastName(faker.name().lastName());
            artist.setShows(new ArrayList<>());
            return artist;
        }, ARTISTS_COUNT);

        // creating event
        var events = fakeMany(() -> {
            var event = new Event();
            event.setEventType(EventType.PLAY);
            event.setTitle(faker.book().title());
            event.setDescription(faker.weather().description());
            event.setDuration((long) ((Math.random() + 60) * 60));
            return event;
        }, EVENTS_COUNT);

        // creating shows
        var shows = fakeMany(() -> {
            var show = new Show();
            show.setEvent(randomChoice(events));
            show.setArtists(randomChoices(artists));
            show.setDateTime(randomDateInRange(365, 365));
            return show;
        }, SHOWS_COUNT);

        shows.forEach(s -> s.getArtists().forEach(a -> a.getShows().add(s)));

        var map = shows.stream().collect(Collectors.toMap(
            Show::getEvent,
            s -> new ArrayList<Show>() {{
                add(s);
            }},
            (a, b) -> {
                a.addAll(b);
                return a;
            }
        ));
        map.forEach(Event::setShows);

        eventRepository.saveAll(events);
        showRepository.saveAll(shows);

        // creating hallplan
        var hallplan = new HallPlan();
        hallPlanRepository.save(hallplan);

        // creating sectors
        var sectors = fakeMany(() -> {
            var sector = new HallSector();
            sector.setName(faker.name().firstName());
            sector.setHallPlan(hallplan);
            sector.setFrontendCoordinates("");
            return sector;
        }, SECTORS_COUNT);

        hallSectorRepository.saveAll(sectors);

        // creating hallspots
        var hallSpots = fakeMany(fakeOneOf(
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
            }), HALLSPOTS_COUNT);

        hallSpotRepository.saveAll(hallSpots);

        // we need to guarantee that no duplicate (show, sector) tuple exists
        var sectorShowsChoiceMap = shows.stream().collect(Collectors.toConcurrentMap(
            s -> s,
            s -> new CopyOnWriteArrayList<HallSector>() {{
                addAll(sectors);
            }}
        ));

        var sectorShowsChosenMap = new ConcurrentHashMap<HallSector, List<Show>>();

        // creating sector-shows
        var sectorShows = fakeMany(() -> {
            var sectorShow = new HallSectorShow();
            var show = randomChoice(shows);
            var sector = randomChoice(sectorShowsChoiceMap.get(show));
            sectorShowsChoiceMap.get(show).remove(sector);

            sectorShow.setShow(show);
            sectorShow.setSector(sector);

            sectorShowsChosenMap.putIfAbsent(sector, new ArrayList<>());
            sectorShowsChosenMap.get(sector).add(show);

            sectorShow.setPrice(faker.random().nextInt(100, 500 * 100));
            return sectorShow;
        }, SECTORS_SHOWS_COUNT);

        hallSectorShowRepository.saveAll(sectorShows);

        // creating tickets
        var tickets = fakeMany(() -> {
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
        }, TICKETS_COUNT);


        // creating order
        var order = new Order(
            tickets,
            customer,
            null
        );
        orderRepository.save(order);

        tickets.forEach(t -> t.setOrder(order));
        ticketRepository.saveAll(tickets);

        // creating invoices
        var invoice1 = new Invoice(
            order,
            LocalDateTime.of(2024, 1, 2, 3, 4, 5),
            InvoiceType.PURCHASE
        );

        var invoices = List.of(invoice1);
        invoiceRepository.saveAll(invoices);
    }

    private <T> List<T> fakeMany(Supplier<T> generator, int count) {
        return IntStream.range(0, count).parallel().mapToObj(i -> generator.get()).collect(Collectors.toList());
    }

    private <T> List<T> fakeMany(Function<Integer, T> generator, int count) {
        return IntStream.range(0, count).mapToObj(generator::apply).toList();
    }

    private <T> List<T> getRandomSubset(List<T> choices) {
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
    private <T> Supplier<T> fakeOneOf(Supplier<T>... generators) {
        var idx = rng.nextInt(generators.length);
        return () -> generators[idx].get();
    }

    private <T> T randomChoice(List<T> choices) {
        var idx = rng.nextInt(choices.size());
        return choices.get(idx);
    }

    private <T> List<T> randomChoices(List<T> choices) {
        return getRandomSubset(choices);
    }

    private LocalDateTime randomDateInRange(int maxPastDays, int maxFutureDays) {
        var date = faker.date().between(
            faker.date().past(maxPastDays, TimeUnit.DAYS),
            faker.date().future(maxFutureDays, TimeUnit.DAYS)
        );
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

}
