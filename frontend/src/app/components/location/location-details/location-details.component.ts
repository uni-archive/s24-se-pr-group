import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LocationDto, LocationEndpointService, ShowEndpointService} from "../../../services/openapi";


@Component({
  selector: 'app-location-details',
  templateUrl: './location-details.component.html',
  styleUrls: ['./location-details.component.scss']
})
export class LocationDetailsComponent implements OnInit {
  locationId: any;
  location: LocationDto = {};

  constructor(
    private route: ActivatedRoute,
    private showService: ShowEndpointService,
    private locationService: LocationEndpointService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const locationId = params['id'];
      this.locationId = locationId;
      this.locationService.findById2(Number(locationId)).subscribe((response) => {
        this.location = response;
      });
    });
  }

  searchLocations = (criteria: any, page: number, size: number) => {
    return this.showService.getShowByLocation(Number(this.locationId));
  }
}
