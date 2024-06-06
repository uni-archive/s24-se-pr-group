package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeAddress;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyArtists;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyEvents;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSectorShows;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSectors;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyHallSpots;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyShows;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeManyTickets;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.fakeUser;
import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorUtils.randomChoices;


/**
 * This class is used to test anything related to cancelling an order.
 * It creates a new user with orders (without or with tickets)
 */
@Profile("generateData")
@Component
public class CancelOrderDataGenerator {

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
        // creating user for order
        var addr = fakeAddress();
        var customer = fakeUser("cancelorder-user@email.com", passwordEncoder, addr);

        var cancellableOrderWithNoTickets = new Order();
        cancellableOrderWithNoTickets.setCustomer(customer);

        var inv1 = new Invoice(
            cancellableOrderWithNoTickets,
            LocalDateTime.now().minusDays(7),
            InvoiceType.PURCHASE
        );
        cancellableOrderWithNoTickets.setInvoices(List.of(inv1));

        var artists = fakeManyArtists(5);
        var events = fakeManyEvents(5);
        var shows = fakeManyShows(events, artists, 5);
        shows.forEach(s -> s.setDateTime(LocalDateTime.now().plusDays(1)));
        var hallplan = new HallPlan();
        var sectors = fakeManyHallSectors(hallplan, 5);
        var hallSpots = fakeManyHallSpots(sectors, 5);
        var sectorShowsChosenMap = new ConcurrentHashMap<HallSector, List<Show>>();
        var sectorShows = fakeManyHallSectorShows(shows, sectors, sectorShowsChosenMap, 10);
        var tickets = fakeManyTickets(hallSpots, sectorShowsChosenMap, 5);

        var cancellableOrderWithTickets = new Order();
        cancellableOrderWithTickets.setCustomer(customer);
        cancellableOrderWithTickets.setTickets(tickets);
        cancellableOrderWithTickets.getTickets().forEach(t -> t.setOrder(cancellableOrderWithTickets));

        var inv2 = new Invoice(
            cancellableOrderWithTickets,
            LocalDateTime.now().minusDays(7),
            InvoiceType.PURCHASE
        );
        cancellableOrderWithTickets.setInvoices(List.of(inv2));

        var nonCancellableOrderExceededPeriod = new Order();
        var inv3 = new Invoice(
            nonCancellableOrderExceededPeriod,
            LocalDateTime.now().minusDays(15),
            InvoiceType.PURCHASE
        );
        nonCancellableOrderExceededPeriod.setCustomer(customer);
        nonCancellableOrderExceededPeriod.setInvoices(List.of(inv3));


        var orderAlreadyCancelled = new Order();
        var inv4 = new Invoice(
            orderAlreadyCancelled,
            LocalDateTime.now().minusDays(7),
            InvoiceType.PURCHASE
        );
        var inv5 = new Invoice(
            orderAlreadyCancelled,
            LocalDateTime.now().minusDays(7),
            InvoiceType.CANCELLATION
        );
        orderAlreadyCancelled.setCustomer(customer);
        var l = List.of(inv4, inv5);
        orderAlreadyCancelled.setInvoices(l);


        var art = fakeManyArtists(5);
        var ev = fakeManyEvents(5);
        var sh = fakeManyShows(ev, art, 1);
        sh.get(0).setDateTime(LocalDateTime.now());
        var hp = new HallPlan();
        var sec = fakeManyHallSectors(hp, 10);
        var hs = fakeManyHallSpots(sec, 10);
        var secSh = fakeManyHallSectorShows(sh, sec, sectorShowsChosenMap, 10);
        var ts = fakeManyTickets(hs, sectorShowsChosenMap, 1);


        var nonCancellableOrderShowAlreadyStarted = new Order();
        var inv6 = new Invoice(
            nonCancellableOrderShowAlreadyStarted,
            LocalDateTime.now().minusDays(7),
            InvoiceType.PURCHASE
        );
        nonCancellableOrderShowAlreadyStarted.setCustomer(customer);
        nonCancellableOrderShowAlreadyStarted.setTickets(ts);
        nonCancellableOrderShowAlreadyStarted.getTickets().forEach(t -> t.setOrder(nonCancellableOrderShowAlreadyStarted));
        nonCancellableOrderShowAlreadyStarted.setInvoices(List.of(inv6));

        // ---
        addressRepository.save(addr);
        userRepository.save(customer);

        orderRepository.save(cancellableOrderWithNoTickets);
        invoiceRepository.save(inv1);

        orderRepository.save(cancellableOrderWithTickets);
        invoiceRepository.save(inv2);
        hallPlanRepository.save(hallplan);
        hallSectorRepository.saveAll(sectors);
        hallSpotRepository.saveAll(hallSpots);
        eventRepository.saveAll(events);
        showRepository.saveAll(shows);
        artistRepository.saveAll(artists);
        ticketRepository.saveAll(tickets);
        hallSectorShowRepository.saveAll(sectorShows);

        orderRepository.save(nonCancellableOrderExceededPeriod);
        invoiceRepository.save(inv3);

        orderRepository.save(orderAlreadyCancelled);
        invoiceRepository.saveAll(l);

        orderRepository.save(nonCancellableOrderShowAlreadyStarted);
        invoiceRepository.save(inv6);
        hallPlanRepository.save(hp);
        hallSectorRepository.saveAll(sec);
        hallSpotRepository.saveAll(hs);
        eventRepository.saveAll(ev);
        showRepository.saveAll(sh);
        artistRepository.saveAll(art);
        ticketRepository.saveAll(ts);
        hallSectorShowRepository.saveAll(secSh);
    }
}
