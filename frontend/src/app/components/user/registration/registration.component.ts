import {AfterViewInit, Component, Input} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserCreateRequest, UserEndpointService,} from "../../../services/openapi";
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";
import {matchPasswords} from "../../../../validators/passwordRepeatValidator";
import {MessagingService} from "../../../services/messaging.service";
import {EventService} from "../../../services/event.service";

@Component({
  selector: "app-registration",
  templateUrl: "./registration.component.html",
  styleUrls: ["./registration.component.scss"],
})
export class RegistrationComponent implements AfterViewInit {
  isLoading = false;
  @Input() noTopMargin: boolean = false;
  @Input() isAdminFlag: boolean = false;
  registrationForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userEndpointService: UserEndpointService,
    private authService: AuthService,
    private router: Router,
    private messagingService: MessagingService,
    private eventService: EventService
  ) {
    this.registrationForm = this.fb.group(
      {
        email: ["", [Validators.required, Validators.email]],
        password: ["", [Validators.required, Validators.minLength(8)]],
        password_repeat: ["", [Validators.required, Validators.minLength(8)]],
        firstName: ["", Validators.required],
        familyName: ["", Validators.required],
        phoneNumber: ["", Validators.required],
        street: ["", Validators.required],
        city: ["", Validators.required],
        zip: ["", Validators.required],
        country: ["Austria", Validators.required],
        isAdmin: [false],
      },
      {validators: matchPasswords}
    );
  }

  ngAfterViewInit(): void {
    if (this.authService.isLoggedIn()) {
      if (!this.isAdminFlag) {
        this.router.navigate([""]);
      }
    }
  }

  onSubmit(): void {
    if (this.registrationForm.valid) {
      this.isLoading = true;
      const newUser: UserCreateRequest = {
        email: this.registrationForm.value.email,
        password: this.registrationForm.value.password,
        firstName: this.registrationForm.value.firstName,
        familyName: this.registrationForm.value.familyName,
        phoneNumber: this.registrationForm.value.phoneNumber,
        isAdmin: this.registrationForm.value.isAdmin,
        addressCreateRequest: {
          street: this.registrationForm.value.street,
          city: this.registrationForm.value.city,
          zip: this.registrationForm.value.zip,
          country: this.registrationForm.value.country,
        },
      };
      this.userEndpointService.register(newUser).subscribe({
        next: (response) => {
          this.messagingService.setMessage(
            "Registrierung erfolgt - bitte aktiviere dein Konto im E-Mail Postfach.",
            "success"
          );
          this.eventService.emitRegistrationSuccess(); // Emit event here
          if (!this.isAdminFlag) {
            this.router.navigate(["/login"], {
              queryParams: {username: newUser.email},
            });
          } else {
            this.registrationForm.reset();
            this.registrationForm.get("country")?.setValue("Austria");
            this.registrationForm.get("isAdmin")?.setValue(false);
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.messagingService.setMessage(error.error, "danger");
          this.isLoading = false;
        },
      });
    }
  }

  checkPasswordsMatch(): boolean {
    const password = this.registrationForm.get("password")?.value;
    const passwordRepeat = this.registrationForm.get("password_repeat")?.value;
    return password === passwordRepeat;
  }
}
