import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-change-password",
  templateUrl: "./change-password.component.html",
  styleUrl: "./change-password.component.scss",
})
export class ChangePasswordComponent implements OnInit {
  token: string | null = null;
  changePasswordForm: FormGroup;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService
  ) {}

  ngOnInit(): void {
    this.changePasswordForm = this.formBuilder.group(
      {
        currentPassword: ["", Validators.required, Validators.minLength(8)],
        newPassword: ["", [Validators.required, Validators.minLength(8)]],
        confirmPassword: ["", Validators.required, Validators.minLength(8)],
      },
      {
        validator: this.mustMatch("newPassword", "confirmPassword"),
      }
    );
  }

  onSubmit(): void {
    this.submitted = true;

    // Stop if the form is invalid
    if (this.changePasswordForm.invalid) {
      return;
    }
    this.token = this.route.snapshot.queryParamMap.get("token");
    const currentPassword =
      this.changePasswordForm.get("currentPassword").value;
    const newPassword = this.changePasswordForm.get("newPassword").value;

    this.userEndpointService
      .setNewPasswordWithValidToken(this.token, newPassword, currentPassword)
      .subscribe({
        next: (response) => {
          this.messagingService.setMessage(response.message, "success");
        },
        error: (error) => {
          this.messagingService.setMessage(error.error, "danger");
        },
        complete: () => {
          console.log("Password change process completed.");
        },
      });
  }

  // Getter for easy access to form fields
  get f() {
    return this.changePasswordForm.controls;
  }

  // Custom validator to check if passwords match
  mustMatch(password: string, confirmPassword: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[password];
      const matchingControl = formGroup.controls[confirmPassword];

      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        return;
      }

      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }
}
