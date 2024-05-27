import { Component, Input, OnInit, SimpleChanges } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { AuthService } from "src/app/services/auth.service";
import { MessagingService } from "src/app/services/messaging.service";
import {
  ApplicationUserResponse,
  UserEndpointService,
} from "src/app/services/openapi";

@Component({
  selector: "app-user-edit",
  templateUrl: "./user-edit.component.html",
  styleUrls: ["./user-edit.component.scss"],
})
export class UserEditComponent implements OnInit {
  @Input() user: ApplicationUserResponse = {};
  userForm: FormGroup;
  editMode: boolean = false;

  constructor(
    private fb: FormBuilder,
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService,
    private authService: AuthService,
    private router: Router
  ) {
    this.createForm();
  }

  ngOnInit() {
    this.loadUserData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.user) {
      this.updateFormValues();
    }
  }

  createForm() {
    this.userForm = this.fb.group({
      id: [this.user.id, Validators.required],
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
    });
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
      });
    }
  }

  async saveUserDetails() {
    if (this.userForm.valid) {
      try {
        const user = await firstValueFrom(
          this.userEndpointService.updateUserInfo(this.userForm.value)
        );
        if (this.user.email !== this.userForm.value.email) {
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
        this.user = user;
        this.updateFormValues();
        this.userForm = this.fb.group({
          id: [this.user.id, Validators.required],
          email: [
            { value: this.user.email, disabled: true },
            [Validators.required, Validators.email],
          ],
          phoneNumber: [
            { value: this.user.phoneNumber, disabled: true },
            Validators.required,
          ],
        });
        this.editMode = false;
      } catch (error) {
        console.error("Error saving user details", error);
      }
    }
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
    this.userForm = this.fb.group({
      id: [this.user.id, Validators.required],
      email: [
        { value: this.user.email, disabled: !this.editMode },
        [Validators.required, Validators.email],
      ],
      phoneNumber: [
        { value: this.user.phoneNumber, disabled: !this.editMode },
        Validators.required,
      ],
    });
  }
}
