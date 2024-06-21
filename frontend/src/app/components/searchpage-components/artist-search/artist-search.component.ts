import {Component, OnInit} from '@angular/core';
import {
    ArtistEndpointService,
    ArtistSearchResponse,
    EventEndpointService,
    EventResponse
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {formatDuration} from "../../../../formatters/durationFormatter";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-artist-search',
    templateUrl: './artist-search.component.html',
    styleUrl: './artist-search.component.scss'
})
export class ArtistSearchComponent implements OnInit {
    filterConfig = {
        firstname: null,
        lastname: null,
        artistName: null,
    }

    constructor(
        private artistService: ArtistEndpointService,
        private messagingService: MessagingService,
        private eventService: EventEndpointService,
        private router: Router,
        private route: ActivatedRoute,
    ) {
    }

    searchArtists = (criteria: typeof this.filterConfig, page: number, size: number) => {
        return this.artistService.search1(
            criteria.firstname,
            criteria.lastname,
            criteria.artistName,
            page,
            size
        );
    }

    pushQueryURL(cfg: any) {
        const obj: typeof this.filterConfig = cfg;
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                "search-type": "ARTIST",
                "search-artist-firstname": obj.firstname,
                "search-artist-lastname": obj.lastname,
                "search-artist-artistname": obj.artistName,
            },
            queryParamsHandling: "merge",
        });
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

    /*
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
    */

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

    ngOnInit(): void {
        const q = this.route.snapshot.queryParams;
        const qFN = q['search-artist-firstname'];
        const qLN = q['search-artist-lastname'];
        const qAN = q['search-artist-artistname'];

        if (qFN) {
            this.filterConfig.firstname = qFN;
        }

        if (qLN) {
            this.filterConfig.lastname = qLN;
        }

        if (qAN) {
            this.filterConfig.artistName = qAN;
        }
    }

    navigateToArtist(artistId: number) {
        this.router.navigate(['/artist', artistId]);
    }
}
