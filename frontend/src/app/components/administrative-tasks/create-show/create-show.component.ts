import {Component, OnInit} from "@angular/core";
import {
    ArtistDto,
    ArtistEndpointService,
    EventDto,
    EventEndpointService,
    HallSectorEndpointService,
    LocationDto,
    LocationEndpointService,
    ShowCreationDto,
    ShowEndpointService,
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subject} from "rxjs";

@Component({
    selector: "app-create-show",
    templateUrl: "./create-show.component.html",
    styleUrl: "./create-show.component.scss",
})
export class CreateShowComponent implements OnInit {
    createForm: FormGroup;
    createDto: ShowCreationDto = {
        dateTime: null,
        event: null,
        artistList: [],
        location: null,
        sectorShowList: [],
    };
    artistList: ArtistDto[] = [];
    location: LocationDto | null = null;
    event: EventDto | null = null;
    clearEvent: Subject<void> = new Subject<void>();

    constructor(
        private showService: ShowEndpointService,
        private hallSectorService: HallSectorEndpointService,
        private locationService: LocationEndpointService,
        private eventService: EventEndpointService,
        private artistService: ArtistEndpointService,
        private router: Router,
        private route: ActivatedRoute,
        private fb: FormBuilder,
        private messagingService: MessagingService
    ) {
        this.createForm = this.fb.group({
            location: ['', Validators.required],
            event: ['', Validators.required],
        });
    }

    ngOnInit(): void {
      const success = this.route.snapshot.queryParams['success'];
      if (success) {
        //this.messagingService.setMessage(success.value);
        console.log(success);
      }
    }

    onArtistsChosen(artists: ArtistDto) {
        //console.log("Chose artist" + artists.artistName);
        this.createDto.artistList.push(artists);
        //console.log(this.createDto.artistList);
    }

    onArtistRemoved(artist: ArtistDto) {
        //console.log(artist);
        //console.log(this.createDto.artistList);
        this.createDto.artistList = this.createDto.artistList.filter(
            (art) => art.id !== artist.id
        );
        //console.log(this.createDto.artistList);
    }

    onArtistChange(search: string) {
        if (search !== null) {
            this.artistService.search1(
                "",
                "",
                search)
                .subscribe({
                    next: (data) => {
                        //console.log(data);
                        this.artistList = data.content.filter((artist) => {
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

    onEventSelected(eventDto: EventDto) {
        this.createDto.event = eventDto;
    }

    onSubmit() {
        //console.log(this.createDto);
        if (
            this.createDto.location !== null &&
            this.createDto.artistList.length !== 0 &&
            this.createDto.event !== null &&
            this.createDto.dateTime !== null
        ) {
            this.showService.createShow(this.createDto).subscribe({
                next: (value) => {
                    //this.createDto = {dateTime: null, event: null, artistList: [], location: null, sectorShowList: []};
                    this.artistList.forEach(artist => this.onArtistRemoved(artist));
                    this.createDto.dateTime = null;
                    //this.priceList = [];
                    //this.sectorList = [];
                  //console.log("SENDING CLEAR EVENT!");
                  this.clearEvent.next();
                  this.router.navigate(
                    ['.'],
                    { relativeTo: this.route, queryParams: { } }
                  );

                    this.messagingService.setMessage(value);

                    //window.location.reload();
                },
                error: (err) => {
                    console.log(err);
                    this.messagingService.setMessage("Fehler.", "warning");
                },
            });
        } else {
            this.messagingService.setMessage(
                "Bitte füllen Sie das Form komplett aus.",
                "warning"
            );
        }
    }

    handleLocationSelected(location: LocationDto): void {
      if (location == null) {
        this.createDto.location = null;
        this.createDto.sectorShowList = [];
        return;
      }
        this.hallSectorService.getShowByLocation1(location.hallPlan.id).subscribe({
            next: (value) => {
                //this.priceList = new Array<number>(value.length);
                value.forEach((val) =>
                    this.createDto.sectorShowList.push({sectorDto: val, price: null})
                );
            },
            error: (err) => console.log(err),
        });
        this.createDto.location = location;
    }

    redirectToCreateLocation(): void {
        this.router.navigate(['locations/create'], {
            queryParams: {
                redirect: "showcreation"
            },
            queryParamsHandling: "merge"
        })
    }

    redirectToCreateEvent(): void {
        this.router.navigate(['eventcreation'], {
            queryParams: {
                redirect: "showcreation"
            },
            queryParamsHandling: "merge"
        })
    }
}
