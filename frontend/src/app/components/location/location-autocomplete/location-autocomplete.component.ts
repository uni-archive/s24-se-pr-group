import {Component, EventEmitter, Output} from '@angular/core';
import { LocationDto, LocationEndpointService } from '../../../services/openapi';

@Component({
  selector: 'app-location-autocomplete',
  templateUrl: './location-autocomplete.component.html',
  styleUrls: ['./location-autocomplete.component.scss']
})
export class LocationAutocompleteComponent {
  @Output() selectedLocation = new EventEmitter<LocationDto>();
  location: LocationDto | null = null;

  constructor(private locationService: LocationEndpointService) {}

  searchLocations = (query: string) => this.locationService.findByName(query);

  onLocationSelected(location: LocationDto): void {
    this.location = location;
    this.selectedLocation.emit(location);
  }
}
