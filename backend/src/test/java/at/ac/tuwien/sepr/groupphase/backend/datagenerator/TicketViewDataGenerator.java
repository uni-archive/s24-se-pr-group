package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.*;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("generateData")
@Component
public class TicketViewDataGenerator {
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
        Faker faker = new Faker();

        // creating user for order
        var customer = new Customer("ticketview-user-48@email.com", "", "tview", "tview", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false);
        customer.setPassword(passwordEncoder.encode("password" + customer.getSalt()));

        userRepository.save(customer);

        // creating event
        var events = fakeMany(i -> {
            var event = new Event();
            event.setEventType(EventType.PLAY);
            event.setTitle(faker.book().title());
            event.setDescription(faker.weather().description());
            event.setDuration((long) ((Math.random() + 60) * 60));
            return event;
        }, 10);

        // creating shows
        var shows = fakeMany(i -> {
            var show = new Show();
            show.setEvent(randomChoice(events));
            show.setDateTime(randomDateInRange(365,365));
            return show;
        }, 100);

        var map = shows.stream().collect(Collectors.toMap(
            Show::getEvent,
            s -> new ArrayList<Show>() {{ add(s); }},
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
        var sectors = fakeMany(i -> {
            var sector = new HallSector();
            sector.setName(faker.name().firstName());
            sector.setHallPlan(hallplan);
            sector.setFrontendCoordinates("");
            return sector;
        }, 20);

        hallSectorRepository.saveAll(sectors);

        // creating hallspots
        var hallSpots = fakeMany(val -> fakeOneOf(
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
            }), 500);

        hallSpotRepository.saveAll(hallSpots);

        // creating sector-shows
        var sectorShows = fakeMany(i -> {
            var sectorShow = new HallSectorShow();
            sectorShow.setShow(randomChoice(shows));
            sectorShow.setSector(randomChoice(sectors));
            sectorShow.setPrice(faker.random().nextInt(100, 500 * 100));
            return sectorShow;
        }, 100);

        hallSectorShowRepository.saveAll(sectorShows);

        // creating tickets
        var tickets = fakeMany(i -> {
            var ticket = new Ticket();
            ticket.setHash(faker.random().hex());
            ticket.setReserved(faker.random().nextBoolean());
            ticket.setValid(faker.random().nextBoolean());

            var sectorShow = randomChoice(sectorShows);
            ticket.setShow(sectorShow.getShow());

            var spotsInSector = hallSpots.stream().filter(spot -> spot.getSector().equals(sectorShow.getSector())).toList();
            ticket.setHallSpot(randomChoice(spotsInSector));
            return ticket;
        }, 100);

        // creating order
        var order = new Order(
            tickets,
            customer
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

    private <T> List<T> fakeMany(Function<Integer, T> generator, int count) {
        return IntStream.range(0, count).mapToObj(generator::apply).toList();
    }

    @SafeVarargs
    private <T> T fakeOneOf(Supplier<T> ...generators) {
        var rng = new Random();
        var idx = rng.nextInt(generators.length);
        return generators[idx].get();
    }

    private <T> T randomChoice(List<T> choices) {
        var rng = new Random();
        var idx = rng.nextInt(choices.size());
        return choices.get(idx);
    }

    private LocalDateTime randomDateInRange(int maxPastDays, int maxFutureDays) {
        var faker = new Faker();
        var date = faker.date().between(
            faker.date().past(365, TimeUnit.DAYS),
            faker.date().future(365, TimeUnit.DAYS)
        );
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

}
