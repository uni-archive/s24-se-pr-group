package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Invoice;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Ticket;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private static final int RANDOM_TICKETS_COUNT = 5000;
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
    private static final Logger log = LoggerFactory.getLogger(TicketViewDataGenerator.class);
    @Autowired
    private DataGenerationConfig dataGenerationConfig;
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
    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

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

        generateRandomTickets();
    }


    private void generateRandomTickets() {
        List<ApplicationUser> users = userRepository.findAll().stream().filter(x->!x.getEmail().equals("cancelorder-user@email.com")).toList(); // e2e tests might fail otherwise
        List<Show> shows = showRepository.findAllWithHallPlans();
        List<Long> hallPlanIds = shows.stream().map(show -> show.getLocation().getHallPlan().getId()).collect(Collectors.toList());
        List<HallSector> allSectors = hallSectorRepository.findSectorsByHallPlanIds(hallPlanIds);

        Map<Long, List<HallSector>> hallPlanToSectorsMap = allSectors.stream().collect(Collectors.groupingBy(hs -> hs.getHallPlan().getId()));
        List<Long> sectorIds = allSectors.stream().map(HallSector::getId).collect(Collectors.toList());
        List<HallSpot> allSpots = hallSpotRepository.findSpotsByHallSectorIds(sectorIds);

        Map<Long, List<HallSpot>> sectorToSpotsMap = allSpots.stream().collect(Collectors.groupingBy(hs -> hs.getSector().getId()));

        for (Show show : shows) {
            HallPlan hallPlan = show.getLocation().getHallPlan();
            hallPlan.setSectors(hallPlanToSectorsMap.get(hallPlan.getId()));
            for (HallSector sector : hallPlan.getSectors()) {
                sector.setSeats(sectorToSpotsMap.get(sector.getId()));
            }
        }
        List<Ticket> tickets = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Invoice> invoices = new ArrayList<>();

        for (int i = 0; i < dataGenerationConfig.ticketAmount; i++) {
            log.info("Generating random ticket " + i + "/" + dataGenerationConfig.ticketAmount);
            ApplicationUser user = users.get(rng.nextInt(users.size()));
            Show show = shows.get(rng.nextInt(shows.size()));
            // Get cached sectors and spots
            List<HallSector> sectors = show.getLocation().getHallPlan().getSectors();
            HallSector selectedSector = sectors.get(rng.nextInt(sectors.size()));
            while(Objects.isNull(selectedSector.getSeats())){
                selectedSector = sectors.get(rng.nextInt(sectors.size()));
            }
            List<HallSpot> spots = selectedSector.getSeats();
            HallSpot hallSpot = spots.get(rng.nextInt(spots.size()));

            Ticket ticket = new Ticket();
            ticket.setShow(show);
            ticket.setHallSpot(hallSpot);

            Order order = new Order();
            order.setCustomer(user);
            order.setTickets(List.of(ticket));
            ticket.setOrder(order);

            boolean invoiced = rng.nextBoolean();
            if (invoiced) {
                Invoice invoice = new Invoice(
                    order,
                    LocalDateTime.now(),
                    InvoiceType.PURCHASE
                );
                invoices.add(invoice);
                order.setInvoices(List.of(invoice));
            } else {
                order.setInvoices(List.of());
            }
            boolean isReserved = rng.nextBoolean();
            ticket.setReserved(isReserved);

            if (invoiced) {
                ticket.setValid(!isReserved);
            } else {
                ticket.setValid(false);
            }

            tickets.add(ticket);
            orders.add(order);
        }

        orderRepository.saveAll(orders);
        ticketRepository.saveAll(tickets);
        invoiceRepository.saveAll(invoices);
    }
}
