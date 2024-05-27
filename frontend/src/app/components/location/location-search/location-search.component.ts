import { Component } from '@angular/core';
import {LocationEndpointService} from "../../../services/openapi";
import {Router} from "@angular/router";

@Component({
  selector: 'app-location-search',
  templateUrl: './location-search.component.html',
  styleUrl: './location-search.component.scss'
})
export class LocationSearchComponent {
  filterConfig = {name: '', city: '', street: '', postalCode: '', country: ''};

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
      true
    );
  };

  navigateToLocation(id: string) {

    this.router.navigate([`/location/${id}`]);

  }
}
