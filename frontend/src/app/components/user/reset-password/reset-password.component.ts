import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-reset-password",
  templateUrl: "./reset-password.component.html",
  styleUrl: "./reset-password.component.scss",
})
export class ResetPasswordComponent {
  token: string | null = null;
  changePasswordForm: FormGroup;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService
  ) {}

  ngOnInit() {
    this.changePasswordForm = this.formBuilder.group(
      {
        newPassword: ["", [Validators.required, Validators.minLength(6)]],
        confirmPassword: ["", Validators.required],
      },
      {
        validator: this.mustMatch("newPassword", "confirmPassword"),
      }
    );
  }

  // Convenience getter for easy access to form fields
  get f() {
    return this.changePasswordForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    // Stop here if the form is invalid
    if (this.changePasswordForm.invalid) {
      return;
    }

    // Handle form submission here
    console.log("Form Submitted", this.changePasswordForm.value);
    this.token = this.route.snapshot.queryParamMap.get("token");
    const newPassword = this.changePasswordForm.get("newPassword").value;
    this.userEndpointService
      .setNewPasswordWithValidToken(this.token, newPassword, null)
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

  // Custom validator to check that two fields match
  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        // Return if another validator has already found an error on the matchingControl
        return;
      }

      // Set error on matchingControl if validation fails
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }
}
