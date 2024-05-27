import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {AutocompleteTextfieldComponent} from "../autocomplete-textfield/autocomplete-textfield.component";
import {LocationDto} from "../../../dtos/LocationDto";
import {DatepickerComponent} from "../../searchpage-components/datepicker/datepicker.component";
import {
  Artist,
  ArtistEndpointService,
  EventDto,
  EventEndpointService,
  ShowCreationDto,
  ShowEndpointService
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {TagSearchComponent} from "../tag-search/tag-search.component";
import {LocationAutocompleteComponent} from "../../location/location-autocomplete/location-autocomplete.component";

@Component({
  selector: 'app-create-show',
  templateUrl: './create-show.component.html',
  styleUrl: './create-show.component.scss'
})
export class CreateShowComponent {

  createDto: ShowCreationDto = {dateTime: null, eventid: null, artists: []};
  eventList: EventDto[] = [];
  locationList: LocationDto[] = [];
  artistList: Artist[] = [];

  location: LocationDto | null = null;

  constructor(private showService: ShowEndpointService, private messagingService: MessagingService, private eventService: EventEndpointService, private artistService: ArtistEndpointService) {
  }

  onEventChosen(event: EventDto) {
    this.createDto.eventid = event.id;
  }

  onArtistsChosen(artists: Artist) {
    console.log("Chose artist" + artists.artistName);
    this.createDto.artists.push(artists);
    console.log(this.createDto.artists);
  }

  onArtistRemoved(artist: Artist) {
    console.log(artist);
    console.log(this.createDto.artists);
    this.createDto.artists = this.createDto.artists.filter(art => art.id !== artist.id);
    console.log(this.createDto.artists);
  }

  onArtistChange(search: string) {
    if (search !== null) {
      this.artistService.search({artistName: search}).subscribe(
        {
          next: (data) => {
            console.log(data);
            this.artistList = data.filter(artist => {
              let ret = true;
              this.createDto.artists.forEach(art => {
                if (art.id === artist.id) {
                  ret = false;
                }
                ;
              });
              return ret;
            });
          },
          error: err => {
            console.log(err);
          }
        }
      );
    }
  }

  onEventChange(search: string) {
    if (search !== null) {
      console.log(search);
      this.eventService.searchEvents({textSearch: search, typ: null, dauer: 0}).subscribe(
        {
          next: (data) => {
            this.eventList = data;
          },
          error: err => {
            console.log(err);
          }
        }
      );
    }
  }

  onSubmit() {
    console.log(this.createDto);
    /*this.showService.createShow({dateTime:this.createDto.dateTime, eventid: this.createDto.eventid}).subscribe({
      next: value => {
        this.messagingService.setMessage(value);
        this.createDto = new ShowCreateDto();
        this.eventList = null;
        this.locationList = null;
      },
      error: err => {
        console.log(err);
        this.messagingService.setMessage("Fehler.", "warning");
      }
    });*/
  }

  handleLocationSelected(location: LocationDto): void {
    this.location = location;
  }
}
