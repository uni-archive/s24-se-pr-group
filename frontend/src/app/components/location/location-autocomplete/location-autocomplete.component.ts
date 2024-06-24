import {Component, EventEmitter, forwardRef, Input, OnInit, Output} from '@angular/core';
import {LocationDto, LocationEndpointService} from '../../../services/openapi';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";

@Component({
  selector: 'app-location-autocomplete',
  templateUrl: './location-autocomplete.component.html',
  styleUrls: ['./location-autocomplete.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => LocationAutocompleteComponent),
    multi: true
  }]
})
export class LocationAutocompleteComponent implements OnInit, ControlValueAccessor {
  @Output() selectedLocation = new EventEmitter<LocationDto>();
  @Input() initialLocation: LocationDto | null;
  @Input() clearEvent: Observable<void> | null;

  location: LocationDto | null = null;

  constructor(
    private locationService: LocationEndpointService,
    private route: ActivatedRoute,
  ) {
  }

  writeValue(obj: LocationDto): void {
    this.location = obj;
    if (obj) {
      this.onLocationSelected(obj);
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

  setDisabledState?(isDisabled: boolean): void {
    // not needed
  }

  searchLocations = (query: string) => this.locationService.findByName1(query);

  onLocationSelected(location: LocationDto): void {
    this.selectedLocation.emit(location);
    this.onChange(this.location);
    this.onTouched();
  }

  ngOnInit(): void {
    if (this.initialLocation) {
      this.writeValue(this.initialLocation);
      return;
    }

    const locationParam = this.route.snapshot.queryParams['select-location'];
    if (locationParam) {
      this.locationService.findById2(locationParam).subscribe({
        next: loc => {
          this.initialLocation = loc;
          this.writeValue(loc);
        }
      });
    }
  }
}
