import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "src/app/services/auth.service";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-update-email",
  templateUrl: "./update-email.component.html",
  styleUrl: "./update-email.component.scss",
})
export class UpdateEmailComponent {
  isLoading = false;
  token: string | null = null;
  constructor(
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private authService: AuthService,
    private router: Router
  ) {}

  changeEmailAddress() {
    this.isLoading = true;
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
}
