import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {LocationDto, LocationEndpointService} from "../../../services/openapi";

@Component({
  selector: 'app-location-overview',
  templateUrl: './location-overview.component.html',
  styleUrls: ['./location-overview.component.scss']
})
export class LocationOverviewComponent implements OnInit {
  locations: LocationDto[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private locationService: LocationEndpointService, private router: Router) { }

  ngOnInit(): void {
    this.fetchLocations();
  }

  fetchLocations(): void {
    this.locationService.findAll1().subscribe({
      next: (data: LocationDto[]) => {
        this.locations = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.isLoading = false;
      }
    });
  }

  navigateToCreate(): void {
    this.router.navigate(['locations/create']);
  }

  navigateToEdit(locationId: number): void {
    this.router.navigate(['locations/edit', locationId]);
  }
}
