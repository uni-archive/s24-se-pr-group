import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";
import { matchPasswords } from "src/validators/passwordRepeatValidator";

@Component({
  selector: "app-reset-password",
  templateUrl: "./reset-password.component.html",
  styleUrl: "./reset-password.component.scss",
})
export class ResetPasswordComponent {
  token: string | null = null;
  restPasswordForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService
  ) {
    this.restPasswordForm = this.formBuilder.group(
      {
        password: ["", [Validators.required, Validators.minLength(8)]],
        password_repeat: ["", [Validators.required, Validators.minLength(8)]],
      },
      { validators: matchPasswords }
    );
  }

  onSubmit() {
    // Stop here if the form is invalid
    if (this.restPasswordForm.invalid) {
      return;
    }

    // Handle form submission here
    this.token = this.route.snapshot.queryParamMap.get("token");
    const newPassword = this.restPasswordForm.get("password").value;
    this.userEndpointService
      .setNewPasswordWithValidToken(this.token, newPassword, null)
      .subscribe({
        next: (response) => {
          this.messagingService.setMessage(response.message, "success");
        },
        error: (error) => {
          this.messagingService.setMessage(error.error, "danger");
        },
      });
  }

  checkPasswordsMatch(): boolean {
    const password = this.restPasswordForm.get("password")?.value;
    const passwordRepeat = this.restPasswordForm.get("password_repeat")?.value;
    return password === passwordRepeat;
  }
}
