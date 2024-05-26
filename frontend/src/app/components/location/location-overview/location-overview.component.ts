import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LocationDto, LocationEndpointService, PageLocationDto } from '../../../services/openapi';

@Component({
  selector: 'app-location-overview',
  templateUrl: './location-overview.component.html',
  styleUrls: ['./location-overview.component.scss']
})
export class LocationOverviewComponent {
  filterConfig = ['name', 'city', 'street', 'postalCode', 'country'];

  constructor(private locationService: LocationEndpointService, private router: Router) {}

  searchLocations = (criteria: any, page: number, size: number) => {
    return this.locationService.search1(
      criteria.name,
      criteria.city,
      criteria.street,
      criteria.postalCode,
      criteria.country,
      page,
      size,
      'name,asc',
      false
    );
  };

  navigateToCreate(): void {
    this.router.navigate(['locations/create']);
  }

  navigateToEdit(locationId: number): void {
    this.router.navigate(['locations/edit', locationId]);
  }
}
