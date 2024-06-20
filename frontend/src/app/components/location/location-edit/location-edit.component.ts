import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HallPlanDto, LocationDto, LocationEndpointService } from '../../../services/openapi';
import { MessagingService } from '../../../services/messaging.service';

@Component({
  selector: 'app-location-edit',
  templateUrl: './location-edit.component.html',
  styleUrls: ['./location-edit.component.scss']
})
export class LocationEditComponent implements OnInit {
  editForm: FormGroup;
  locationId: number;
  addressId: number;
  selectedHallPlan: HallPlanDto | null = null;

  constructor(
    private fb: FormBuilder,
    private locationService: LocationEndpointService,
    private router: Router,
    private route: ActivatedRoute,
    private messagingService: MessagingService
  ) {
    this.editForm = this.fb.group({
      name: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', Validators.required],
      country: ['', Validators.required],
      hallPlan: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.locationId = +this.route.snapshot.paramMap.get('id');
    this.loadLocationData();
  }

  loadLocationData(): void {
    this.locationService.findById2(this.locationId).subscribe({
      next: (data: LocationDto) => {
        this.editForm.patchValue({
          name: data.name,
          street: data.address?.street,
          city: data.address?.city,
          zip: data.address?.zip,
          country: data.address?.country,
          hallPlan: data.hallPlan
        });
        this.addressId = data.address?.id;
        this.selectedHallPlan = data.hallPlan;
      },
      error: (error) => {
        this.messagingService.setMessage('Error loading location data', 'error');
        console.error('Error loading location data:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.editForm.valid) {
      const updatedLocation: LocationDto = {
        id: this.locationId,
        name: this.editForm.value.name,
        address: {
          id: this.addressId,
          street: this.editForm.value.street,
          city: this.editForm.value.city,
          zip: this.editForm.value.zip,
          country: this.editForm.value.country
        },
        hallPlan: this.editForm.value.hallPlan
      };

      this.locationService.update(this.locationId, updatedLocation).subscribe({
        next: () => this.router.navigate(['/locations']),
        error: (error) => {
          this.messagingService.setMessage('Error updating location: ' + error.error, 'error');
          console.error('Error updating location:', error);
        }
      });
    }
  }

  onHallPlanSelected(hallPlan: HallPlanDto): void {
    this.selectedHallPlan = hallPlan;
    this.editForm.patchValue({ hallPlan });
  }

  onHallPlanReset(): void {
    this.selectedHallPlan = null;
    this.editForm.patchValue({ hallPlan: null });
  }
}
