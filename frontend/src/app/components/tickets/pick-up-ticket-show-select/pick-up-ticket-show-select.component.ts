import {Component} from '@angular/core';
import {ShowSearchDto} from "../../../dtos/ShowSearchDto";
import {LocationDto, ShowEndpointService, ShowListDto} from "../../../services/openapi";
import {Router} from "@angular/router";
import {formatDuration} from 'src/formatters/durationFormatter';
import {MessagingService} from "../../../services/messaging.service";

@Component({
  selector: 'app-pick-up-ticket-show-select',
  templateUrl: './pick-up-ticket-show-select.component.html',
  styleUrl: './pick-up-ticket-show-select.component.scss'
})
export class PickUpTicketShowSelectComponent {

  searchData: ShowSearchDto = new ShowSearchDto();
  time: Date = null;
  shows: ShowListDto[] = [];
  location: LocationDto = null;

  constructor(private service: ShowEndpointService, private router: Router, private messageService: MessagingService) {
  }

  onSubmit() {
    console.log(this.searchData);
    this.reloadShows();
  }

  reloadShows() {
    let dto: ShowSearchDto = {
      dateTime: this.searchData.dateTime,
      price: this.searchData.price * 100,
      eventId: this.searchData.eventId,
      location: this.location?.id
    };
    this.service.searchShows(
      dto.price,
      dto.dateTime,
      dto.location
    ).subscribe({
      next: (data) => {
        this.shows = data.content;
      },
      error: (err) => {
        this.messageService.setMessage("Error while loading shows", "danger");
      }
    });
  }

  ngOnInit() {
    this.reloadShows();
  }

  artistMap(artist) {
    if (artist.artistName) {
      return artist.artistName + " ";
    } else {
      return artist.firstName + " " + artist.lastName + " ";
    }
  }

  formatDateStr(date: String): String {
    let splitDatetime = date.split("T");
    let splitDate = splitDatetime[0].split("-");
    let splitTime = splitDatetime[1].split(":");
    return splitDate[2] + "." + splitDate[1] + "." + splitDate[0] + " " + splitTime[0] + ":" + splitTime[1];
  }

  protected readonly formatDuration = formatDuration;
  protected readonly Date = Date;

  navigateToPickup(id: number) {
    this.router.navigate(['tickets/pickup', id]);
  }

  setLocation($event: LocationDto) {
    this.location = $event;
  }
}
