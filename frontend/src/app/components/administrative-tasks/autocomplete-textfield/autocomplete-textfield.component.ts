import {Component, EventEmitter, Input, Output} from '@angular/core';
import {EventDto, EventEndpointService} from "../../../services/openapi";
@Component({
  selector: 'app-autocomplete-textfield',
  templateUrl: './autocomplete-textfield.component.html',
  styleUrl: './autocomplete-textfield.component.scss'
})
export class AutocompleteTextfieldComponent {

  @Output() selectedEvent = new EventEmitter<EventDto>();
  event: EventDto | null = null;

  constructor(private eventService: EventEndpointService) {}

  searchEvents = (query: string) => this.eventService.searchEvents({textSearch:query, typ:null, dauer:0});

  onEventSelected(event: EventDto): void {
    this.event = event;
    this.selectedEvent.emit(event);
  }

  textExtraction(item : EventDto) : string {
    return item.title;
  }

}
