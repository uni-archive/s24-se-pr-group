import { Component, Input, OnInit, SimpleChanges } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { firstValueFrom } from "rxjs";
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
    private messagingService: MessagingService
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
      email: [this.user.email, [Validators.required, Validators.email]],
      firstName: [this.user.firstName, Validators.required],
      familyName: [this.user.familyName, Validators.required],
      phoneNumber: [this.user.phoneNumber, Validators.required],
    });
  }

  async loadUserData() {
    try {
      const user = await firstValueFrom(this.userEndpointService.getUser());
      this.user = user;
      this.updateFormValues();
    } catch (error) {
      console.error("Error loading user data", error);
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
        this.user = user;
        this.updateFormValues();
        this.messagingService.setMessage(
          "Benutzer erfolgreich aktualisiert.",
          "success"
        );
        this.editMode = false;
      } catch (error) {
        console.error("Error saving user details", error);
      }
    }
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
  }
}
