import {Component, OnInit} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {ShowSearchDto} from "../../../dtos/ShowSearchDto";
import {DatepickerComponent} from "../datepicker/datepicker.component";
import {ShowEndpointService, ShowListDto} from "../../../services/openapi";
import {formatDuration} from "../../../../formatters/durationFormatter";
import {Router} from "@angular/router";

@Component({
  selector: "app-show-search",
  standalone: true,
  imports: [FormsModule, BrowserModule, DatepickerComponent],
  templateUrl: "./show-search.component.html",
  styleUrl: "./show-search.component.scss",
})
export class ShowSearchComponent implements OnInit {
  searchData: ShowSearchDto = new ShowSearchDto();
  time: Date = null;
  shows: ShowListDto[] = [];
  protected readonly formatDuration = formatDuration;
  protected readonly Date = Date;

  constructor(private service: ShowEndpointService, private router: Router) {
  }

  onSubmit() {
    console.log(this.searchData);
    //TODO: LOCATION IMPLEMENTATION
    this.reloadShows();
  }

  reloadShows() {
    let dto: ShowSearchDto = {
      dateTime: this.searchData.dateTime,
      price: this.searchData.price * 100,
      eventId: this.searchData.eventId,
      location: null
    ,
    };
    this.service.searchShows(dto).subscribe({
      next: (data) => {
        this.shows = data;
      },
      error: (err) => {
        //TODO: INFORM USER
        console.log(err);
      },
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
    return (splitDate[2] + "." + splitDate[1] + "." + splitDate[0] + " " + splitTime[0] + ":" + splitTime[1]);
  }

  navigateToPickup(id: number) {
    this.router.navigate(['tickets/pickup', id]);
  }
}
