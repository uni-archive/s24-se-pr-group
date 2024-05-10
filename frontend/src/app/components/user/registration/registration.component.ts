import { AfterViewInit, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserCreateRequest, UserEndpointService } from "../../../services/openapi";
import { AuthService } from "../../../services/auth.service";
import { Router } from "@angular/router";
import { matchPasswords } from "../../../../validators/passwordRepeatValidator";
import { MessagingService } from "../../../services/messaging.service";
import { AngularPhoneNumberInput } from 'angular-phone-number-input';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements AfterViewInit {
  registrationForm: FormGroup;

  constructor(private fb: FormBuilder, private userEndpointService: UserEndpointService, private authService: AuthService, private router: Router, private messagingService: MessagingService) {
    this.registrationForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      password_repeat: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', Validators.required],
      familyName: ['', Validators.required],
      phoneNumber: [''],
      isAdmin: [false]
    }, { validators: matchPasswords });
  }

  ngAfterViewInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/home']);
    }
  }

  onSubmit(): void {
    if (this.registrationForm.valid) {
      // Concatenate country code and phone number

      const newUser: UserCreateRequest = { ...this.registrationForm.value };
      this.userEndpointService.register(newUser).subscribe({
        next: (response) => {
          this.messagingService.setMessage('User registered successfully. You can now log in.', 'success');
          this.router.navigate(['/login'], { queryParams: { username: newUser.email } })
        },
        error: (error) => console.error('Error registering user:', error)
      });
    }
  }
}
