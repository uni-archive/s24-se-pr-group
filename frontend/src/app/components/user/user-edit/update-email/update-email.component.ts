import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-update-email",
  templateUrl: "./update-email.component.html",
  styleUrl: "./update-email.component.scss",
})
export class UpdateEmailComponent {
  token: string | null = null;
  constructor(
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private router: Router
  ) {}

  changeEmailAddress() {
    this.token = this.route.snapshot.queryParamMap.get("token");
    if (this.token == null || this.token == "") {
      this.messagingService.setMessage(
        "Keinen gültigen Token gefunden.",
        "danger"
      );
      return;
    }
    this.userEndpointService
      .updateUserEmailWithValidToken(this.token)
      .subscribe({
        next: (response: { message: string }) => {
          this.messagingService.setMessage(response.message, "success");
          this.router.navigate(["/login"]);
        },
        error: (error) => {
          this.messagingService.setMessage(error.error, "danger");
        },
      });
  }
}
