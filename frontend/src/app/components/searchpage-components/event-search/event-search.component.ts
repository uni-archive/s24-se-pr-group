import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import { BrowserModule } from '@angular/platform-browser';
import {EventSearch} from "../../../dtos/EventSearchDto";
import {EventService} from "../../../services/event.service";
import {EventDto, EventResponse} from "../../../services/openapi";
import {formatDuration} from "../../../../formatters/durationFormatter";


@Component({
  selector: 'app-event-search',
  templateUrl: './event-search.component.html',
  standalone: true,
  imports: [
    FormsModule,
    BrowserModule
  ],
  styleUrl: './event-search.component.scss'
})
export class EventSearchComponent implements OnInit{
  public eventTypes: String[] = [null, "CONCERT","THEATER", "PLAY"];
  searchData : EventSearch = new EventSearch();
  events : EventDto[] = [];

  constructor(private service: EventService) {

  }

  ngOnInit() {
    this.refreshEvents();
  }

  refreshEvents() {
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


