import {Component, OnInit} from '@angular/core';
import {LocationEndpointService} from "../../../services/openapi";
import {ActivatedRoute, Router} from "@angular/router";
import {LocationDto} from "../../../dtos/LocationDto";

@Component({
    selector: 'app-location-search',
    templateUrl: './location-search.component.html',
    styleUrl: './location-search.component.scss'
})
export class LocationSearchComponent implements OnInit {
    filterConfig = {name: '', city: '', street: '', postalCode: '', country: ''};

    constructor(
        private locationService: LocationEndpointService,
        private router: Router,
        private route: ActivatedRoute,
    ) {
    }

    searchLocations = (criteria: any, page: number, size: number) => {
        return this.locationService.search(
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

    pushQueryURL(obj: any) {
        const cfg: typeof this.filterConfig = obj;
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                "search-type": "LOCATION",
                "search-location-name": cfg.name,
                "search-location-city": cfg.city,
                "search-location-street": cfg.street,
                "search-location-postalCode": cfg.postalCode,
                "search-location-country": cfg.country,
            },
            queryParamsHandling: "merge",
        });
    }

    navigateToLocation(id: string) {
        this.router.navigate([`/location/${id}`]);
    }

    ngOnInit(): void {
        const q = this.route.snapshot.queryParams;
        const qName = q["search-location-name"];
        const qCity = q["search-location-city"];
        const qStreet = q["search-location-street"];
        const qPostalCode = q["search-location-postalCode"];
        const qCountry = q["search-location-country"];

        if (qName) {
            this.filterConfig.name = qName;
        }

        if (qCity) {
            this.filterConfig.city = qCity;
        }

        if (qStreet) {
            this.filterConfig.street = qStreet;
        }

        if (qPostalCode) {
            this.filterConfig.postalCode = qPostalCode;
        }

        if (qCountry) {
            this.filterConfig.country = qCountry;
        }
    }

    protected readonly LocationDto = LocationDto;
}
