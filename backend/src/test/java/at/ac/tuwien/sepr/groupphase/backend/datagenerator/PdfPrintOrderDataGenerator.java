package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.Customer;
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

@Profile("generateData")
@Component
public class PdfPrintOrderDataGenerator {

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
        var customer = new Customer("pdf-user-51@email.com", "", "pdf", "pdf", "+431234567890", "zyxwvutsrqponmlkjihgfedcba", 0, false);
        customer.setPassword(passwordEncoder.encode("password" + customer.getSalt()));

        userRepository.save(customer);

        // creating event
        var event = new Event();
        event.setEventType(EventType.PLAY);
        event.setTitle("event title");
        event.setDescription("event description");
        event.setDuration(60 * 60L);

        // creating shows
        var show1 = new Show(LocalDateTime.of(2024, 5, 12, 11, 12, 13), List.of(), event);
        var show2 = new Show(LocalDateTime.of(2024, 5, 13, 10, 11, 12), List.of(), event);

        var shows = List.of(
            show1,
            show2
        );

        event.setShows(shows);

        eventRepository.save(event);
        showRepository.saveAll(shows);

        // creating hallplan
        var hallplan = new HallPlan();
        hallPlanRepository.save(hallplan);

        // creating sectors
        var sector1 = new HallSector(
            hallplan,
            "sector 1",
            "",
            List.of()
        );
        var sector2 = new HallSector(
            hallplan,
            "sector 2",
            "",
            List.of()
        );
        var sectors = List.of(
            sector1,
            sector2
        );
        hallSectorRepository.saveAll(sectors);

        // creating hallspots
        var hallspot1 = new HallSeat(sector1, "45;68");
        var hallspot2 = new HallSpot(sector1);
        var hallspot3 = new HallSpot(sector1);
        var hallspot4 = new HallSpot(sector1);
        var hallspot5 = new HallSpot(sector1);
        var hallspot6 = new HallSpot(sector2);
        var hallspot7 = new HallSpot(sector2);
        var hallspot8 = new HallSpot(sector2);
        var hallspot9 = new HallSpot(sector2);
        var hallspot10 = new HallSpot(sector2);

        var hallspots = List.of(
            hallspot1,
            hallspot2,
            hallspot3,
            hallspot4,
            hallspot5,
            hallspot6,
            hallspot7,
            hallspot8,
            hallspot9,
            hallspot10
        );
        hallSpotRepository.saveAll(hallspots);

        // creating tickets
        var ticket1 = new Ticket("hash1", false, false, hallspot1, show1, null);
        var ticket2 = new Ticket("hash2", false, true, hallspot2, show2, null);
        var ticket3 = new Ticket("hash3", true, false, hallspot3, show2, null);
        var ticket4 = new Ticket("hash4", true, true, hallspot4, show1, null);
        var ticket5 = new Ticket("hash5", true, true, hallspot5, show2, null);

        var ticket6 = new Ticket("hash6", true, false, hallspot6, show2, null);
        var ticket7 = new Ticket("hash7", false, true, hallspot7, show1, null);
        var ticket8 = new Ticket("hash8", false, false, hallspot8, show2, null);
        var ticket9 = new Ticket("hash9", false, false, hallspot9, show2, null);
        var ticket10 = new Ticket("hash10", false, true, hallspot10, show1, null);

        var tickets = List.of(
            ticket1,
            ticket2,
            ticket3,
            ticket4,
            ticket5,
            ticket6,
            ticket7,
            ticket8,
            ticket9,
            ticket10
        );

        // creating sector-shows
        var sectorShow1 = new HallSectorShow(show1, sector1, 10 * 100);
        var sectorShow2 = new HallSectorShow(show2, sector1, 20 * 100);
        var sectorShow3 = new HallSectorShow(show1, sector2, 30 * 100);
        var sectorShow4 = new HallSectorShow(show2, sector2, 40 * 100);
        var sectorShows = List.of(sectorShow1, sectorShow2, sectorShow3, sectorShow4);
        hallSectorShowRepository.saveAll(sectorShows);

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

        var invoice2 = new Invoice(
            order,
            LocalDateTime.of(2024, 2, 3, 4, 5, 6),
            InvoiceType.CANCELLATION
        );
        var invoices = List.of(invoice1, invoice2);
        invoiceRepository.saveAll(invoices);
    }

}
