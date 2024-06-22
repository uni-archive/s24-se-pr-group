import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "src/app/services/auth.service";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";
import { matchPasswords } from "src/validators/passwordRepeatValidator";

@Component({
  selector: "app-change-password",
  templateUrl: "./change-password.component.html",
  styleUrl: "./change-password.component.scss",
})
export class ChangePasswordComponent {
  isLoading = false;
  token: string | null = null;
  changePasswordForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private authService: AuthService,
    private router: Router
  ) {
    this.changePasswordForm = this.formBuilder.group(
      {
        currentPassword: ["", [Validators.required, Validators.minLength(8)]],
        password: ["", [Validators.required, Validators.minLength(8)]],
        password_repeat: ["", [Validators.required, Validators.minLength(8)]],
      },
      { validators: matchPasswords }
    );
  }

  onSubmit(): void {
    // Stop if the form is invalid
    if (this.changePasswordForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.token = this.route.snapshot.queryParamMap.get("token");
    const currentPassword =
      this.changePasswordForm.get("currentPassword").value;
    const newPassword = this.changePasswordForm.get("password").value;

    this.userEndpointService
      .setNewPasswordWithValidToken(this.token, newPassword, currentPassword)
      .subscribe({
        next: (response) => {
          this.messagingService.setMessage(response.message, "success");
          if (this.authService.isLoggedIn()) {
            this.authService.logoutUser();
          }
          this.router.navigate(["/login"]);
          this.isLoading = false;
        },
        error: (error) => {
          this.messagingService.setMessage(error.error, "danger");
          this.isLoading = false;
        },
      });
  }

  checkPasswordsMatch(): boolean {
    const password = this.changePasswordForm.get("password")?.value;
    const passwordRepeat =
      this.changePasswordForm.get("password_repeat")?.value;
    return password === passwordRepeat;
  }
}
