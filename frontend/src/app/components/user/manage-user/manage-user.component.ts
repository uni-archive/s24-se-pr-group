import { Component } from "@angular/core";
import { EmailValidator } from "@angular/forms";
import { UserSearch } from "src/app/dtos/user-search";
import { MessagingService } from "src/app/services/messaging.service";
import {
  ApplicationUserResponse,
  UserEndpointService,
} from "src/app/services/openapi";

@Component({
  selector: "app-manage-user",
  templateUrl: "./manage-user.component.html",
  styleUrl: "./manage-user.component.scss",
})
export class ManageUserComponent {
  searchParams: UserSearch = {
    email: "",
  };
  user: ApplicationUserResponse = null;

  constructor(
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService
  ) {}

  searchUser() {
    this.userEndpointService
      .findUserByEmail(this.searchParams.email)
      .subscribe({
        next: (response) => {
          this.user = response;
          this.messagingService.setMessage("User loaded successfully.");
        },
        error: (error) => console.error("Error loading user:", error),
      });
  }

  updateLockStatus(status: boolean) {}
}
