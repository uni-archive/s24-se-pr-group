import {Component, OnInit} from '@angular/core';
import {
  ArtistDto,
  ArtistEndpointService, ArtistSearchResponse,
  LocationDto,
  LocationEndpointService,
  ShowEndpointService
} from "../../../services/openapi";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-artist-details',
  templateUrl: './artist-details.component.html',
  styleUrl: './artist-details.component.scss'
})
export class ArtistDetailsComponent implements OnInit {
  artistId: any;
  artist: ArtistDto = {};

  constructor(
      private route: ActivatedRoute,
      private showService: ShowEndpointService,
      protected router: Router,
      private artistService: ArtistEndpointService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const artistId = params['id'];
      this.artistId = artistId;
      this.artistService.findById4(artistId).subscribe({
        next: artist => {
          this.artist = artist;
        },
        error: err => {
          this.router.navigate(['/search']);
        }
      });
    });
  }

  searchShowsByArtist = (criteria: any, page: number, size: number) => {
    return this.showService.getShowsByArtistId(Number(this.artistId));
  }

  formatArtistName(artist: ArtistSearchResponse): string {
    const firstAndLast = [artist.firstName, artist.lastName]
        .filter(n => n !== null && n !== undefined && n.trim() !== "")
        .join(' ');

    if (artist.artistName) {
      return `${artist.artistName} (${firstAndLast})`;
    } else {
      return firstAndLast;
    }
  }
}
