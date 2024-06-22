import {Component, ViewChild} from '@angular/core';
import {HallplanComponent, HallSeat, HallSection} from "../hallplan/hallplan.component";
import {
  HallPlanEndpointService,
  OrderEndpointService,
  ShowEndpointService, ShowHallplanResponse,
  ShowResponse,
  TicketEndpointService
} from "../../services/openapi";
import {MessagingService} from "../../services/messaging.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {SeatEntity, SectionEntity} from "../hallplan/entities";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { HallSectorDto } from "../../services/openapi";
import {HttpStatusCode} from "@angular/common/http";
import {forkJoin, Observable} from "rxjs";
import {formatPrice} from "../../../formatters/currencyFormatter";

@Component({
  selector: 'app-ticket-select',
  standalone: true,
  imports: [
    HallplanComponent,
    NgForOf,
    NgIf,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './ticket-select.component.html',
  styleUrl: './ticket-select.component.scss'
})
export class TicketSelectComponent {
  @ViewChild('hallplan') hallplan: HallplanComponent;
  showResponse: ShowResponse | null = null;
  date: string = "";
  time: string = "";
  showHallplan: ShowHallplanResponse | null = null;
  selectedSeats: HallSeat[] = [];
  selectedSectors: number[] = [];
  visibleSectors: HallSectorDto[] = [];
  sectorSeatMap: ({ [key: number]: HallSeat[] }) = {};
  selectedSectorSpots: ({ [key: string]: number }) = {};
  orderId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private showService: ShowEndpointService,
    private ticketService: TicketEndpointService,
    private orderService: OrderEndpointService,
    private messagingService: MessagingService,
    private router: Router,
  ) {
  }

  get calcPrice(): number {
    return this.selectedSeats.reduce((acc, seat) => acc + this.findSectorById(seat.sectorId).price, 0) +
    this.selectedSectors.reduce((acc, sectorId) => acc + this.findSectorById(sectorId).price * (this.selectedSectorSpots[String(sectorId)]), 0);
  }

  ngOnInit(): void {
    this.loadOrderCookie();
    this.ticketService.configuration.withCredentials = true;

    this.showService.getShowById(this.route.snapshot.params.id)
      .subscribe({
        next: show => {
          this.showResponse = show;
          const date = new Date(show.dateTime);
          this.date = date.toLocaleDateString('de-DE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
          });
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          this.time = `${hours}:${minutes}`;
        },
        error: err => {
          console.log(err)
          this.messagingService.setMessage("Ihre Tickets konnten nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
        }
      });

    this.showService.getAvailableSeatsByShowId(this.route.snapshot.params.id)
      .subscribe({
        next: async (showHallplan) => {
          this.showHallplan = showHallplan;
          await this.hallplan.setBackgroundImage(this.showHallplan.backgroundImage);
          this.hallplan.sections = this.showHallplan.sectors.map(section => {
            return {
              id: section.id,
              name: section.name,
              color: section.color,
              isStandingOnly: section.standingOnly,
              availableSpotCount: section.availableSpotCount,
              price: section.price,
              points: JSON.parse(section.frontendCoordinates),
              seats: section.spots.map(seat => {

                return {
                  id: seat.id,
                  sectorId: section.id,
                  isAvailable: ! seat.isReserved,
                  pos: JSON.parse(seat.frontendCoordinates),
                }
              })
            }
          });
          this.hallplan.generateEntities();
          this.hallplan.refreshCanvas(true);
        },
        error: err => {
          console.log(err)
          this.messagingService.setMessage("Ihre Tickets konnten nicht geladen werden. Bitte versuchen Sie es später erneut.", 'danger');
        }
      });
  }

  loadOrderCookie(): void {
    this.orderService.configuration.withCredentials = true;
    this.orderService.getCurrentOrder().subscribe({
      next: order => {
        this.orderId = order.id;
        console.log("current", order);
      },
      error: err => {
        if(err.status === HttpStatusCode.BadRequest) {
          this.orderService.createOrder().subscribe({
            next: order => {
              this.orderId = order.id;
              console.log("create", order);
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

  updateSectorSpotCount(event: any, sectorId: number) {
    console.log("event fired", event)
    this.selectedSectorSpots[String(sectorId)] = Number(event.target.value);
  }

  deselectSeat(seat: HallSeat) {
    this.hallplan.deselectSeat(seat.id);
  }

  findSeatById(seatId: number, sectorId: number) {
    return this.findSectorById(sectorId).seats.find(seat => seat.id === seatId);
  }

  findSectorById(sectorId: number): HallSection {
    return this.hallplan.sections.find(section => section.id === sectorId);
  }
  addToCart(isReservation: boolean) {
    forkJoin([
      ...this.selectedSectors.map(sectorId => {
        const arrOut: Observable<any>[] = [];
        for (let i = 0; i < this.selectedSectorSpots[String(sectorId)]; i++) {
          arrOut.push(this.ticketService.addSectionTicket({
            sectorId: sectorId,
            orderId: this.orderId,
            showId: this.showResponse.id,
            reservationOnly: isReservation
          }));
        }
        return arrOut;
      }).reduce((acc, val) => acc.concat(val), []),
      ...this.selectedSeats.map(seat => this.ticketService.addTicket({
        spotId: seat.id,
        orderId: this.orderId,
        showId: this.showResponse.id,
        reservationOnly: isReservation
    }))
    ]).subscribe({
      next: () => {
        this.messagingService.setMessage("Ihre Tickets wurden erfolgreich hinzugefügt.", 'success');
        this.router.navigate(['/user/cart']);
      },
      error: err => {
        this.messagingService.setMessage("Ihre Tickets konnten nicht hinzugefügt werden. Bitte versuchen Sie es später erneut.", 'danger');
        this.router.navigate(['/user/cart']);
        console.log(err);
      }
    });
  }

  ngAfterViewInit() {
    this.hallplan.onSelectedEntitiesChange = (selectedEntities) => {
      this.selectedSeats = selectedEntities.filter(entity => entity.constructor.name === 'SeatEntity').map(entity => (entity as SeatEntity).data);
      // map selected seats into sectorSeatMap
      this.sectorSeatMap = this.selectedSeats.reduce((acc, seat) => {
        if (acc[seat.sectorId]) {
          acc[seat.sectorId].push(seat);
        } else {
          acc[seat.sectorId] = [seat];
        }
        return acc;
      }, {});

      this.selectedSectors = selectedEntities.filter(entity => entity.constructor.name === 'SectionEntity')
        .filter(entity => (entity as SectionEntity).data.isStandingOnly)
        .map(entity => (entity as SectionEntity).data.id);

      this.selectedSectors.forEach(sectorId => this.selectedSectorSpots[String(sectorId)] = 1);

      this.visibleSectors = [...Object.keys(this.sectorSeatMap).map(sectorId => this.findSectorById(Number(sectorId))),
        ...this.selectedSectors.map(sectorId => this.findSectorById(sectorId))];
    }
  }


  protected readonly JSON = JSON;
  protected readonly formatPrice = formatPrice;
}
