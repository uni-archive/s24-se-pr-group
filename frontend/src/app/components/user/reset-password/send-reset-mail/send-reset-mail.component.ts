import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthService } from "src/app/services/auth.service";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-send-reset-mail",
  templateUrl: "./send-reset-mail.component.html",
  styleUrl: "./send-reset-mail.component.scss",
})
export class SendResetMailComponent {
  isLoading = false;
  resetPasswordForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService,
    private authService: AuthService,
    private router: Router
  ) {
    this.resetPasswordForm = this.formBuilder.group({
      email: ["", [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    if (this.resetPasswordForm.valid) {
      this.isLoading = true;
      console.log(this.isLoading);
      const email = this.resetPasswordForm.get("email")?.value;
      this.userEndpointService.sendEmailForPasswordReset(email).subscribe({
        next: (response) => {
          this.messagingService.setMessage(response.message, "success");
          if (this.authService.isLoggedIn()) {
            this.authService.logoutUser();
          }
          this.router.navigate(["/login"]);
          this.isLoading = false;
        },
        error: (error) => {
          console.error("Fehler beim Senden der Passwort-Reset-E-Mail", error);
          this.isLoading = false;
        },
      });
    }
  }
}
