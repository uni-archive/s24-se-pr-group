import {Component, OnInit} from '@angular/core';
import {
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

  orderCookie(): string {
    return this.cookieService.get('order');
  }

  loadOrder(): void {
    this.orderService.configuration.withCredentials = true;
    const orderCookie = this.orderCookie();
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

        }

        this.messagingService.setMessage("Ihre Bestellung konnte nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
        // this.router.navigate(["/"]);
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
