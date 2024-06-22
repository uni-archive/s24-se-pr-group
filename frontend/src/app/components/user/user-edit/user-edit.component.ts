import {
  Component,
  Input,
  OnInit,
  SimpleChanges,
  OnChanges,
} from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { AuthService } from "src/app/services/auth.service";
import { MessagingService } from "src/app/services/messaging.service";
import {
  ApplicationUserResponse,
  UserEndpointService,
  UserUpdateInfoRequest,
} from "src/app/services/openapi";

@Component({
  selector: "app-user-edit",
  templateUrl: "./user-edit.component.html",
  styleUrls: ["./user-edit.component.scss"],
})
export class UserEditComponent implements OnInit, OnChanges {
  @Input() user: ApplicationUserResponse = {
    id: 0,
    email: "",
    firstName: "",
    familyName: "",
    phoneNumber: "",
    address: {
      id: 0,
      street: "",
      zip: "",
      city: "",
      country: "Austria",
    },
  };
  userForm: FormGroup;
  editMode: boolean = false;
  userForDelete: ApplicationUserResponse = {};

  constructor(
    private fb: FormBuilder,
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService,
    private authService: AuthService,
    private router: Router
  ) {
    this.userForm = this.fb.group({
      id: [{ value: this.user.id, disabled: true }, Validators.required],
      email: [
        { value: this.user.email, disabled: true },
        [Validators.required, Validators.email],
      ],
      firstName: [
        { value: this.user.firstName, disabled: true },
        Validators.required,
      ],
      familyName: [
        { value: this.user.familyName, disabled: true },
        Validators.required,
      ],
      phoneNumber: [
        { value: this.user.phoneNumber, disabled: true },
        Validators.required,
      ],
      street: [
        { value: this.user.address.street, disabled: true },
        Validators.required,
      ],
      zip: [
        { value: this.user.address.zip, disabled: true },
        Validators.required,
      ],
      city: [
        { value: this.user.address.city, disabled: true },
        Validators.required,
      ],
      country: [
        { value: this.user.address.country, disabled: true },
        Validators.required,
      ],
    });
  }

  ngOnInit() {
    this.loadUserData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.user) {
      this.updateFormValues();
    }
  }

  async loadUserData() {
    try {
      const user = await firstValueFrom(this.userEndpointService.getUser());
      this.user = user;
      this.updateFormValues();
    } catch (error) {
      console.error("Error loading user data", error);
      if (this.authService.isLoggedIn()) {
        this.messagingService.setMessage(
          "Fehler beim Laden der Benutzerdaten. Bitte erneut anmelden.",
          "error"
        );
        this.authService.logoutUser();
        this.router.navigate(["/login"]);
        return;
      }
    }
  }

  updateFormValues() {
    if (this.userForm) {
      this.userForm.patchValue({
        id: this.user.id,
        email: this.user.email,
        firstName: this.user.firstName,
        familyName: this.user.familyName,
        phoneNumber: this.user.phoneNumber,
        street: this.user.address?.street || "",
        zip: this.user.address?.zip || "",
        city: this.user.address?.city || "",
        country: this.user.address?.country || "",
      });
    }
  }

  async saveUserDetails() {
    if (this.userForm.valid) {
      try {
        const newData: UserUpdateInfoRequest = {
          id: this.user.id,
          email: this.userForm.value.email,
          phoneNumber: this.userForm.value.phoneNumber,
          address: {
            id: this.user.address.id,
            street: this.userForm.value.street,
            zip: this.userForm.value.zip,
            city: this.userForm.value.city,
            country: this.userForm.value.country,
          },
        };
        const user = await firstValueFrom(
          this.userEndpointService.updateUserInfo(newData)
        );
        this.user = user;
        if (this.user.email !== this.userForm.value.email) {
          this.authService.logoutUser();
          this.messagingService.setMessage(
            "Bestätige bitte deine neue E-Mail-Adresse.",
            "success"
          );
        } else {
          this.messagingService.setMessage(
            "Benutzer erfolgreich aktualisiert.",
            "success"
          );
        }
        this.updateFormValues();
        this.editMode = false;
        this.userForm.disable(); // Disable the form after saving
      } catch (error) {
        this.messagingService.setMessage(error.error, "danger");
      }
    }
  }

  toggleEditMode() {
    if (this.editMode) {
      this.updateFormValues();
    }
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.userForm.enable();
      this.userForm.get("id").disable(); // Keep id disabled
      this.userForm.get("firstName").disable(); // Keep id disabled
      this.userForm.get("familyName").disable(); // Keep id disabled
    } else {
      this.userForm.disable();
    }
  }

  changePassword() {
    this.userEndpointService
      .sendEmailForPasswordChange(this.user.email)
      .subscribe({
        next: (response) => {
          this.messagingService.setMessage(response.message, "success");
        },
        error: (error) => {
          this.messagingService.setMessage(error.error.message, "danger");
        },
      });
  }

  public requestDeleteUser(user: ApplicationUserResponse): void {
    this.userForDelete = user;
  }

  deleteAccount() {
    this.userEndpointService.deleteUser(this.user.id).subscribe({
      next: (response) => {
        this.messagingService.setMessage(response.message, "success");
        this.authService.logoutUser();
        this.router.navigate(["/login"]);
      },
      error: (error) => {
        this.messagingService.setMessage(error.error.message, "danger");
      },
    });
  }
}
