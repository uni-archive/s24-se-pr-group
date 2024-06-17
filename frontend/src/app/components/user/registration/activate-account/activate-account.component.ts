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
  token: string | null = null;
  constructor(
    private userEndpointService: UserEndpointService,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private router: Router
  ) {}

  activateAccount() {
    this.token = this.route.snapshot.queryParamMap.get("token");
    this.userEndpointService.activateAccount(this.token).subscribe({
      next: (response) => {
        this.messagingService.setMessage(response.message, "success");
        this.router.navigate(["/login"]);
      },
      error: (error) => {
        console.error("Failed to activate account", error);
      },
    });
  }
}
