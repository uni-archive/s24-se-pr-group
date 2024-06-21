import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import { BrowserModule } from '@angular/platform-browser';
import {EventSearch} from "../../../dtos/EventSearchDto";
import {EventService} from "../../../services/event.service";
import {EventDto, EventResponse} from "../../../services/openapi";
import {formatDuration} from "../../../../formatters/durationFormatter";
import {NgForOf, NgIf} from "@angular/common";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {fromEvent, Observable} from "rxjs";
@Component({
  selector: 'app-event-search',
  templateUrl: './event-search.component.html',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf,
    ReactiveFormsModule,
  ],
  styleUrl: './event-search.component.scss'
})
export class EventSearchComponent implements OnInit, AfterViewInit {
  public eventTypes: String[] = ["CONCERT","THEATER", "PLAY"];
  searchData : EventSearch = new EventSearch();
  events : EventDto[] = [];

  @ViewChild('searchForm') searchForm;
  submits: Observable<any> | null;

  constructor(
    private service: EventService,
  ) {
  }

  ngOnInit() {
    this.refreshEvents();
  }

  ngAfterViewInit() {
    this.submits = fromEvent(this.searchForm.nativeElement, 'keydown').pipe(debounceTime(300));
    this.submits.subscribe({
      next: () => {
        this.refreshEvents();
      }
    })
  }


  refreshEvents() {
    if (this.searchData.dauer == null) {
      this.searchData.dauer = 0;
    }
    this.service.getEvents(this.searchData).pipe().subscribe(
      {
        next:(data)=> {
          console.log(
            data
          );
          this.events = data;
        },
        error:(err) => {
          //todo show to user
          console.log(err);
        }
      }
    );
  }

  onSubmit() {
    console.log(this.searchData);
    this.refreshEvents();
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


