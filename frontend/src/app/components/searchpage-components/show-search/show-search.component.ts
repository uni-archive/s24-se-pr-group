import {Component, OnInit} from "@angular/core";
import {ShowSearchDto} from "../../../dtos/ShowSearchDto";
import {ArtistDto, ShowEndpointService, ShowListDto} from "../../../services/openapi";
import {formatDuration} from "../../../../formatters/durationFormatter";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: "app-show-search",
  templateUrl: "./show-search.component.html",
  styleUrl: "./show-search.component.scss",
})
export class ShowSearchComponent implements OnInit {
  filterConfig = {
    dateTime: null,
    price: null,
    location: null,
  }
  protected readonly formatDuration = formatDuration;
  protected readonly Date = Date;

  constructor(
    private service: ShowEndpointService,
    protected router: Router,
    private route: ActivatedRoute,
  ) {
  }

  searchShows = (criteria: typeof this.filterConfig, page: number, size: number) => {
    return this.service.searchShows(
      criteria.price * 100,
      criteria.dateTime,
      criteria.location,
      page,
      size,
    );
  }

  pushQueryURL(cfg: any) {
    const obj: typeof this.filterConfig = cfg;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        "search-type": "SHOW",
        "search-show-price": obj.price || null,
        "search-show-date": obj.dateTime,
        "search-show-location": obj.location || null,
      },
      queryParamsHandling: "merge",
    })
  }

  navigateToTicketSelect(showId: number) {
    this.router.navigate(['show', showId, 'ticket-select']);
  }

  ngOnInit() {
    const q = this.route.snapshot.queryParams;
    const qPrice = q['search-show-price'];
    const qDate = q['search-show-date'];
    const qLocation = q['search-show-location'];

    if (qPrice) {
      this.filterConfig.price = qPrice;
    }

    if (qDate) {
      this.filterConfig.dateTime = qDate;
    }

    if (qLocation) {
      this.filterConfig.location = qLocation;
    }
  }

  artistMap(artist: ArtistDto) {
    if (artist.artistName) {
      return artist.artistName + " ";
    } else {
      return artist.firstName + " " + artist.lastName + " ";
    }
  }

  formatDateStr(date: String):
    String {
    let splitDatetime = date.split("T");
    let splitDate = splitDatetime[0].split("-");
    let splitTime = splitDatetime[1].split(":");
    return (splitDate[2] + "." + splitDate[1] + "." + splitDate[0] + " " + splitTime[0] + ":" + splitTime[1]);
  }

  navigateToPickup(id: number) {
    this.router.navigate(['tickets/pickup', id]);
  }
}
