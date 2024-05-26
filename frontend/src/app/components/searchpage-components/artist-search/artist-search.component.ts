import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {
  ArtistEndpointService,
  ArtistSearchResponse,
  EventEndpointService,
  EventResponse
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {NgForOf, NgIf} from "@angular/common";
import {formatDuration} from "../../../../formatters/durationFormatter";

@Component({
  selector: 'app-artist-search',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './artist-search.component.html',
  styleUrl: './artist-search.component.scss'
})
export class ArtistSearchComponent {
  firstname: string;
  lastname: string;
  artistName: string;

  foundArtists: ArtistSearchResponse[];
  eventsOfArtist: EventResponse[];
  showEvents: boolean = false;

  constructor(
    private artistService: ArtistEndpointService,
    private messagingService: MessagingService,
    private eventService: EventEndpointService,
  ) {
  }

  onSubmit(): void {
    this.showEvents = false;
    this.artistService.search({firstName: this.firstname, lastName: this.lastname, artistName: this.artistName})
      .subscribe({
        next: res => {
          this.foundArtists = res;
        },
        error: err => {
          this.messagingService.setMessage("Künstler*innen konnten nicht gesucht werden. Bitte versuchen Sie es später erneut.", 'error')
        }
      })
  }

  formatArtistName(artist: ArtistSearchResponse): string {
    const firstAndLast = [ artist.firstName, artist.lastName ]
      .filter(n => n !== null && n !== undefined && n.trim() !== "")
      .join(' ');

    if(artist.artistName) {
      return `${artist.artistName} (${firstAndLast})`;
    } else {
      return firstAndLast;
    }
  }

  viewEventsForArtist(artist: ArtistSearchResponse): void {
    this.eventService.findByArtist(artist.id).subscribe({
      next: events => {
        this.eventsOfArtist = events;
        this.showEvents = true;
      },
      error: err => {
        this.messagingService.setMessage(
          `Konnte keine Veranstaltungen für ${this.formatArtistName(artist)} suchen. Bitte versuchen Sie es später erneut.`,
          'error'
        );
      }
    })
  }

  formatEventType(event: EventResponse.EventTypeEnum): string {
    switch (event) {
      case "THEATER":
        return "Theaterstück";
      case "PLAY":
        return "Play";
      case "CONCERT":
        return "Konzert";
    }
  }

  protected readonly formatDuration = formatDuration;
}
