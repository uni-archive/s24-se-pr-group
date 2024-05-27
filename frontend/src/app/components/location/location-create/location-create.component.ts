import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {LocationCreateRequest, LocationEndpointService} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";


@Component({
  selector: 'app-location-create',
  templateUrl: './location-create.component.html',
  styleUrls: ['./location-create.component.scss']
})
export class LocationCreateComponent {
  createForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private locationService: LocationEndpointService,
    private router: Router,
    private messagingService: MessagingService
  ) {
    this.createForm = this.fb.group({
      name: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      zip: ['', Validators.required],
      country: ['Austria', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.createForm.valid) {
      const newLocation: LocationCreateRequest = {
        name: this.createForm.value.name,
        addressCreateRequest: {
          street: this.createForm.value.street,
          city: this.createForm.value.city,
          zip: this.createForm.value.zip,
          country: this.createForm.value.country,
        },
      };

      this.locationService.create1(newLocation).subscribe({
        next: () => {
          this.router.navigate(['/locations'])
          this.messagingService.setMessage('Location created successfully', 'success');
        },
        error: (error) => {
          this.messagingService.setMessage('Error creating location: ' + error.error, 'error');
          console.error('Error creating location:', error)
        },
      });
    }
  }
}
