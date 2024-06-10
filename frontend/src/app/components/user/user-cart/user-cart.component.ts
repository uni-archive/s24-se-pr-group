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
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {formatPrice} from "../../../../formatters/currencyFormatter";
import {CookieService} from "ngx-cookie-service";
import {HttpStatusCode} from "@angular/common/http";

@Component({
  selector: 'app-user-cart',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf
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
    private cookieService: CookieService
  ) {
  }

  ngOnInit(): void {
    this.loadOrder();
  }

  changeTicketReserved(ticketId: number, setReserved: boolean): void {
    const ticket = this.order.tickets.find(t => t.id === ticketId);
    if(ticket.reserved === setReserved) {
      return;
    }

    this.ticketService.changeTicketReserved(ticketId, setReserved).subscribe({
      next: t => {
        this.loadOrder();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  removeTicket(ticketId: number): void {
    this.ticketService.removeTicket(ticketId).subscribe({
      next: () => {
        this.loadOrder();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  getEventById(eventId: number): EventResponse {
    return this.order.tickets
      .map(t => t.show.event)
      .find(e => e.id === eventId);
  }

  getSubtotalForEvent(eventId: number): string {
    const ticketsForEvent = this.ticketsBySectorsByEvent.get(eventId);
    let sum = 0;
    for(const tickets of ticketsForEvent.values()) {
      sum += tickets
        .filter(t => !t.reserved)
        .map(t => t.hallSpot.sector.hallSectorShow.price)
        .reduce((p1, p2) => p1 + p2, 0);
    }
    return formatPrice(sum);
  }

  getTotalPrice(): string {
    const total = this.order.tickets
      .filter(t => !t.reserved)
      .map(t => t.hallSpot.sector.hallSectorShow.price)
      .reduce((p1, p2) => p1 + p2, 0);
    return formatPrice(total);
  }

  purchaseOrder(): void {
    this.orderService.purchaseOrder(this.order.id).subscribe({
      next: () => {
        this.messagingService.setMessage("Die Bestellung war erfolgreich!");
        this.router.navigate(["/"]);
      },
      error: err => {
        console.log(err);
      }
    });
  }

  loadOrder(): void {
    this.orderService.configuration.withCredentials = true;
    this.orderService.getCurrentOrder().subscribe({
      next: order => {
        this.order = order;
        console.log(this.order);
        this.ticketsBySectorsByEvent = this.createEventToTicketMap();
      },
      error: err => {
        if(err.status === HttpStatusCode.BadRequest) {
          this.orderService.createOrder().subscribe({
            next: order => {
              this.order = order;
              this.ticketsBySectorsByEvent = this.createEventToTicketMap();
            },
            error: err => {
              this.messagingService.setMessage("Ihre Bestellung konnte nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
              console.log(err);
            }
          })

        } else {
          this.messagingService.setMessage("Ihre Bestellung konnte nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
        }

      }
    });

  }

  createEventToTicketMap(): Map<number, Map<number, TicketDetailsResponse[]>> {
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

  addStandSectorTicket(orderId: number, sectorId: number): void {
    /*
        this.orderService.addStandTicket(orderId, sectorId).subscribe({
          next: () => {
            this.ticketsBySectorsByEvent.get(orderId).get(sectorId).length++;
          },
          error: err => {
            if(err.status === HttpStatusCode.InternalServerError) {
              this.messagingService.setMessage("Stehplatz konnte nicht gebucht werden. Bitte versuchen Sie es später erneut.", "danger");
            }
            this.messagingService.setMessage("Weitere Stehplätze sind leider bereits ausgebucht.", "danger");
          }
        });
    */
  }


  removeStandSectorTicket(orderId: number, sectorId: number): void {
    /*
        this.orderService.removeStandTicket(orderId, sectorId).subscribe({
          next: () => {
            this.ticketsBySectorsByEvent
          },
          error: err => {
              this.messagingService.setMessage("Stehplatz konnte nicht freigegeben werden. Bitte versuchen Sie es später erneut.", "danger");
          }
        });
    */
  }

  protected readonly formatPrice = formatPrice;
}
