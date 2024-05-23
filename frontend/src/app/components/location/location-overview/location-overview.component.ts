import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LocationDto, LocationEndpointService, PageLocationDto } from "../../../services/openapi";
import { FormBuilder, FormGroup } from '@angular/forms';
import { debounceTime } from 'rxjs/operators';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-location-overview',
  templateUrl: './location-overview.component.html',
  styleUrls: ['./location-overview.component.scss']
})
export class LocationOverviewComponent implements OnInit {
  locations: LocationDto[] = [];
  error: string | null = null;
  searchForm: FormGroup;
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;

  constructor(
    private locationService: LocationEndpointService,
    private router: Router,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.searchForm = this.fb.group({
      name: [''],
      city: [''],
      street: [''],
      postalCode: [''],
      country: ['']
    });
  }

  ngOnInit(): void {
    this.searchForm.valueChanges
    .pipe(debounceTime(300)) // Adjust debounce time as needed
    .subscribe(() => {
      this.currentPage = 1; // Reset to first page on new search
      this.searchLocations();
    });

    this.searchLocations();
  }

  searchLocations(): void {
    this.error = null;
    const searchCriteria = this.searchForm.value;
    this.locationService.search(
      searchCriteria.name,
      searchCriteria.city,
      searchCriteria.street,
      searchCriteria.postalCode,
      searchCriteria.country,
      this.currentPage - 1,
      this.pageSize,
      'name,asc'
    ).subscribe({
      next: (data: PageLocationDto) => {
        this.locations = data.content;
        this.totalPages = data.totalPages;
        this.cdr.detectChanges(); // Trigger change detection manually
      },
      error: (err) => {
        this.error = err.message;
        this.cdr.detectChanges(); // Trigger change detection manually
      }
    });
  }

  changePage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.searchLocations();
    }
  }

  getPages(): number[] {
    const pages = [];
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  navigateToCreate(): void {
    this.router.navigate(['locations/create']);
  }

  navigateToEdit(locationId: number): void {
    this.router.navigate(['locations/edit', locationId]);
  }
}
