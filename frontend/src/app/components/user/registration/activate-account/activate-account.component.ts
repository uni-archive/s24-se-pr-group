import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MessagingService } from "src/app/services/messaging.service";
import { UserEndpointService } from "src/app/services/openapi";

@Component({
  selector: "app-activate-account",
  templateUrl: "./activate-account.component.html",
  styleUrl: "./activate-account.component.scss",
})
export class ActivateAccountComponent {
  isLoading = false;
  token: string | null = null;
  constructor(
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private router: Router
  ) {}

  activateAccount() {
    this.isLoading = true;
    this.token = this.route.snapshot.queryParamMap.get("token");
    if (this.token == null || this.token == "") {
      this.messagingService.setMessage(
        "Keinen gültigen Token gefunden.",
        "danger"
      );
      return;
    }
    this.userEndpointService.activateAccount(this.token).subscribe({
      next: (response) => {
        this.messagingService.setMessage(response.message, "success");
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
