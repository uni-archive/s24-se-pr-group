import {Component, ViewChild} from '@angular/core';
import {HallplanComponent, HallSeat, HallSection} from "../hallplan/hallplan.component";
import {
  HallPlanEndpointService, HallSector,
  ShowEndpointService, ShowHallplanResponse,
  ShowResponse,
  TicketEndpointService
} from "../../services/openapi";
import {MessagingService} from "../../services/messaging.service";
import {ActivatedRoute} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {SeatEntity, SectionEntity} from "../hallplan/entities";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

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
  visibleSectors: HallSector[] = [];
  sectorSeatMap: ({ [key: number]: HallSeat[] }) = {};

  constructor(
    private route: ActivatedRoute,
    private showService: ShowEndpointService,
    private hallplanService: HallPlanEndpointService,
    private messagingService: MessagingService
  ) {
  }

  get calcPrice(): number {
    return this.selectedSeats.reduce((acc, seat) => acc + this.findSectorById(seat.sectorId).price, 0);
  }

  ngOnInit(): void {
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

  findSeatById(seatId: number, sectorId: number) {
    return this.findSectorById(sectorId).seats.find(seat => seat.id === seatId);
  }

  findSectorById(sectorId: number): HallSection {
    return this.hallplan.sections.find(section => section.id === sectorId);
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

      this.visibleSectors = [...Object.keys(this.sectorSeatMap).map(sectorId => this.findSectorById(Number(sectorId))),
        ...this.selectedSectors.map(sectorId => this.findSectorById(sectorId))];
    }
  }


  protected readonly JSON = JSON;
}
