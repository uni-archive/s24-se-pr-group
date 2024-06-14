import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-send-reset-mail",
  templateUrl: "./send-reset-mail.component.html",
  styleUrl: "./send-reset-mail.component.scss",
})
export class SendResetMailComponent {
  resetPasswordForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService
  ) {
    this.resetPasswordForm = this.formBuilder.group({
      email: ["", [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    if (this.resetPasswordForm.valid) {
      const email = this.resetPasswordForm.get("email")?.value;
      console.log(email);
      this.userEndpointService.sendEmailForPasswordReset(email).subscribe({
        next: (response) => {
          console.log("Passwort-Reset-E-Mail gesendet", response);
          this.messagingService.setMessage(response.message, "success");
        },
        error: (error) => {
          console.error("Fehler beim Senden der Passwort-Reset-E-Mail", error);
        },
      });
    }
  }
}
