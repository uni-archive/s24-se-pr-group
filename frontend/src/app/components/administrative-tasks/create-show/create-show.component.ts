import {Component} from '@angular/core';
import {
  ArtistDto,
  ArtistEndpointService,
  EventDto,
  EventEndpointService, HallSectorEndpointService,
  ShowCreationDto,
  ShowEndpointService,
  LocationDto
} from "../../../services/openapi";
import {Artist} from "src/app/services/openapi/model/artist";
import {MessagingService} from "../../../services/messaging.service";
import {ShowCreateDto} from "../../../dtos/ShowCreateDto";

@Component({
  selector: "app-create-show",
  templateUrl: "./create-show.component.html",
  styleUrl: "./create-show.component.scss",
})
export class CreateShowComponent {
  createDto: ShowCreationDto = {dateTime: null, event: null, artistList: [], location: null, sectorShowList: []};
  artistList: ArtistDto[] = [];
  location: LocationDto | null = null;

  constructor(
    private showService: ShowEndpointService,
    private hallSectorService : HallSectorEndpointService, privatemessagingService: MessagingService,
    private eventService: EventEndpointService,
    private artistService: ArtistEndpointService
  ) {
  }


  onArtistsChosen(artists: ArtistDto) {
    console.log("Chose artist" + artists.artistName);
    this.createDto.artistList.push(artists);
    console.log(this.createDto.artistList);
  }

  onArtistRemoved(artist: ArtistDto) {
    console.log(artist);
    console.log(this.createDto.artistList);
    this.createDto.artistList = this.createDto.artistList.filter(
      (art) => art.id !== artist.id
    );
    console.log(this.createDto.artistList);
  }

  onArtistChange(search: string) {
    if (search !== null) {
      this.artistService.search({artistName: search}).subscribe({
        next: (data) => {
          console.log(data);
          this.artistList = data.filter((artist) => {
            let ret = true;
            this.createDto.artistList.forEach((art) => {
              if (art.id === artist.id) {
                ret = false;
              }
            });
            return ret;
          });
        },
        error: (err) => {
          console.log(err);
        },
      });
    }
  }

  onEventSelected(eventDto : EventDto) {
    this.createDto.event = eventDto;
  }

  onSubmit() {
    console.log(this.createDto);
    if (this.createDto.location !== null &&
      this.createDto.artistList.length !== 0 &&
      this.createDto.event !== null &&
      this.createDto.dateTime !== null) {
      this.showService.createShow(this.createDto).subscribe({
        next: value => {
          //this.createDto = {dateTime: null, eventDto: null, artists: [], locationDto: null};
          //this.artistList = [];
          //this.priceList = [];
          //this.sectorList = [];

          this.messagingService.setMessage(value);

          //window.location.reload();
        },
        error: err => {
          console.log(err);
          this.messagingService.setMessage("Fehler.", "warning");
        }
      });
    } else {
      this.messagingService.setMessage("Bitte füllen Sie das Form komplett aus.", "warning");
    }
  }

  handleLocationSelected(location: LocationDto): void {
    this.hallSectorService.getShowByLocation1(location.id).subscribe({
      next: value => {
        //this.priceList = new Array<number>(value.length);
        console.log(value);
        value.forEach(val => this.createDto.sectorShowList.push({sectorDto: val, price: null}));
      },
      error: err => console.log(err)
    });
    this.createDto.location = location;
  }
}
