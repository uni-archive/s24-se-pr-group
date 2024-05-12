import { Component } from "@angular/core";
import { UserSearch } from "src/app/dtos/user-search";
import { AuthService } from "src/app/services/auth.service";
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
    private messagingService: MessagingService,
    private authService: AuthService
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

  updateLockStatus(status: boolean) {
    if (this.user == null) {
      console.error("No user loaded.");
      return;
    }

    this.userEndpointService.getUser().subscribe({
      next: (currentUser) => {
        if (currentUser == null) {
          console.error("No current user loaded.");
          return;
        }

        if (this.user.id != currentUser.id) {
          this.user.accountLocked = status;
          this.userEndpointService
            .updateUserStatusByEmail(this.user)
            .subscribe({
              next: (response) => {
                this.user = response;
                this.messagingService.setMessage("User updated successfully.");
              },
              error: (error) => console.error("Error loading user:", error),
            });
        } else {
          console.error("Cannot update own user.");
          this.messagingService.setMessage("Cannot update own user.", "danger");
        }
      },
      error: (error) => console.error("Error loading user:", error),
    });
  }
}
