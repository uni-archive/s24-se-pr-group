import {Component, OnInit} from '@angular/core';
import {
  EventResponse,
  OrderDetailsResponse,
  OrderEndpointService,
  TicketDetailsResponse,
  TicketEndpointService
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {Router} from "@angular/router";
import {KeyValuePipe, NgForOf} from "@angular/common";
import {formatPrice} from "../../../../formatters/currencyFormatter";

@Component({
  selector: 'app-user-cart',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe
  ],
  templateUrl: './user-cart.component.html',
  styleUrl: './user-cart.component.scss'
})
export class UserCartComponent implements OnInit {
  order: OrderDetailsResponse | null = null;
  ticketsBySectorsByEvent: Map<number, Map<number, TicketDetailsResponse[]>> = new Map();

  constructor(
    private orderService: OrderEndpointService,
    private ticketService: TicketEndpointService,
    private messagingService: MessagingService,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.loadOrder();
  }

  loadOrder(): void {
    // TODO: Order-Cookie
    this.orderService.findById1(7).subscribe({
      next: order => {
        this.order = order;
        this.ticketsBySectorsByEvent = this.createEventToTicketMap();
      },
      error: error => {
        this.messagingService.setMessage("Ihre Bestellung konnte nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
        // this.router.navigate(["/"]);
      }
    });
  }

  createEventToTicketMap(): Map<number, Map<number, TicketDetailsResponse[]>>{
    const map = new Map<number, Map<number, TicketDetailsResponse[]>>();
    this.order.tickets.forEach(ticket => {
      const event = ticket.show.event;
      const sector = ticket.hallSpot.sector;
      if (map.has(event.id)) {
        const sectors = map.get(event.id);
        if (sectors.has(sector.id)) {
          sectors.get(sector.id).push(ticket);
        } else {
          sectors.set(sector.id, [ticket]);
        }
      } else {
        const sectors = new Map<number, TicketDetailsResponse[]>();
        sectors.set(sector.id, [ticket]);
        map.set(event.id, sectors);
      }
    });
    return map;
  }

  protected readonly formatPrice = formatPrice;
}
