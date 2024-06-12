package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.ArtistRepository;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeAddress;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyArtists;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyEvents;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSectorShows;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSectors;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSpots;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyOrders;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyShows;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyTickets;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeUser;

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
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @PostConstruct
    private void generateData() {
        var addr = fakeAddress();
        var customer = fakeUser("ticketview-user-48@email.com", passwordEncoder, addr);
        var artists = fakeManyArtists(ARTISTS_COUNT);
        var events = fakeManyEvents(EVENTS_COUNT);
        var shows = fakeManyShows(events, artists, SHOWS_COUNT);
        var hallplan = new HallPlan();
        var sectors = fakeManyHallSectors(hallplan, SECTORS_COUNT);
        var hallSpots = fakeManyHallSpots(sectors, HALLSPOTS_COUNT);
        var sectorShowsChosenMap = new ConcurrentHashMap<HallSector, List<Show>>();
        var sectorShows = fakeManyHallSectorShows(shows, sectors, sectorShowsChosenMap, SECTORS_SHOWS_COUNT);
        var tickets = fakeManyTickets(hallSpots, sectorShowsChosenMap, TICKETS_COUNT);
        var order = fakeManyOrders(tickets, List.of(customer), 1).get(0);
        var invoice1 = new Invoice(
            order,
            LocalDateTime.of(2024, 1, 2, 3, 4, 5),
            InvoiceType.PURCHASE
        );
        order.setInvoices(List.of(invoice1));
        addressRepository.save(addr);
        userRepository.save(customer);
        orderRepository.save(order);
        invoiceRepository.save(invoice1);
        hallPlanRepository.save(hallplan);
        hallSectorRepository.saveAll(sectors);
        hallSpotRepository.saveAll(hallSpots);
        eventRepository.saveAll(events);
        showRepository.saveAll(shows);
        artistRepository.saveAll(artists);
        ticketRepository.saveAll(tickets);
        hallSectorShowRepository.saveAll(sectorShows);
    }


}
