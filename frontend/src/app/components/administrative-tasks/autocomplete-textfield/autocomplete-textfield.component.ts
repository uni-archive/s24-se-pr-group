import {Component, EventEmitter, forwardRef, Input, OnInit, Output} from '@angular/core';
import {EventDto, EventEndpointService} from "../../../services/openapi";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {map, Observable} from "rxjs";

@Component({
  selector: "app-autocomplete-textfield",
  templateUrl: "./autocomplete-textfield.component.html",
  styleUrl: "./autocomplete-textfield.component.scss",
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => AutocompleteTextfieldComponent),
    multi: true
  }]
})
export class AutocompleteTextfieldComponent implements OnInit, ControlValueAccessor {

  @Output() selectedEvent = new EventEmitter<EventDto>();
  @Input() clearEvent: Observable<void> | null;
  @Input() initialEvent: EventDto | null;
  event: EventDto | null = null;

  constructor(
    private eventService: EventEndpointService,
    private route: ActivatedRoute,
  ) {
  }

  searchEvents = (query: string) => {
    return this.eventService.searchEvents(
        query,
        null,
        null,
    ).pipe(map(r => r.content));
  }

  onEventSelected(event: EventDto): void {
    this.selectedEvent.emit(event);
    this.onChange(this.event);
    this.onTouched();
  }

  textExtraction(item: EventDto): string {
    return item.title;
  }

  ngOnInit(): void {
    //this.clearEvent?.subscribe(()=>console.log("OBSERVABLE TRIGGERED!!!!!"));
    if (this.initialEvent) {
      this.writeValue(this.initialEvent);
    }

    const eventParam = this.route.snapshot.queryParams['select-event'];
    if (eventParam) {
      this.eventService.findById3(eventParam).subscribe({
        next: event => {
          this.initialEvent = event;
          this.writeValue(event);
        }
      });
    }
  }

  private onChange: any = () => {
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  private onTouched: any = () => {
  };

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(obj: any): void {
    this.event = obj;
    if (obj) {
      this.onEventSelected(obj);
    }
  }
}
