import {Component, OnInit} from '@angular/core';
import {EventEndpointService, EventResponse} from "../../../services/openapi";
import {formatDuration} from "../../../../formatters/durationFormatter";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-event-search',
  templateUrl: './event-search.component.html',
  styleUrl: './event-search.component.scss'
})
export class EventSearchComponent implements OnInit {
  public eventTypes: string[] = ["CONCERT", "THEATER", "PLAY"];
  filterConfig = {
    typ: null,
    dauer: null,
    textSearch: null,
  };

  constructor(
    private service: EventEndpointService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
  }

  searchEvents = (criteria: typeof this.filterConfig, page: number, size: number) => {
    return this.service.searchEvents(
      criteria.textSearch,
      criteria.typ === 'Alle' ? null : criteria.typ,
      criteria.dauer,
      page,
      size
    );
  }

  pushQueryURL(obj: any) {
    const cfg = obj as any;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        "search-type": "EVENT",
        "search-event-name": cfg.textSearch,
        "search-event-duration": cfg.dauer || null,
        "search-event-type": cfg.typ
      },
      queryParamsHandling: "merge",
    });
  }

  ngOnInit() {
    const q = this.route.snapshot.queryParams;
    const qName = q['search-event-name'];
    const qDur = q['search-event-duration'];
    const qType = q['search-event-type'];

    if (qName) {
      this.filterConfig.textSearch = qName;
    }

    if (qDur) {
      this.filterConfig.dauer = qDur;
    }

    if (qType) {
      this.filterConfig.typ = qType;
    }
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

  navigateToEvent(eventId: number) {
    this.router.navigate(['/event/', eventId]);
  }
}


