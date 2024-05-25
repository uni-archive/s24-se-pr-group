import { Component } from '@angular/core';
import { LocationDto, LocationEndpointService } from '../../../services/openapi';

@Component({
  selector: 'app-location-autocomplete',
  templateUrl: './location-autocomplete.component.html',
  styleUrls: ['./location-autocomplete.component.scss']
})
export class LocationAutocompleteComponent {
  selectedLocation: LocationDto | null = null;

  constructor(private locationService: LocationEndpointService) {}

  searchLocations = (query: string) => this.locationService.findByName(query);

  onLocationSelected(location: LocationDto): void {
    this.selectedLocation = location;
    console.log('Selected location:', location);
  }
}
