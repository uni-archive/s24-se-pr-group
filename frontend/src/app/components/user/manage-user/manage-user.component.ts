import { Component } from "@angular/core";
import { MessagingService } from "src/app/services/messaging.service";
import {
  ApplicationUserDto,
  ApplicationUserResponse,
  UserEndpointService,
} from "src/app/services/openapi";

@Component({
  selector: "app-manage-user",
  templateUrl: "./manage-user.component.html",
  styleUrls: ["./manage-user.component.scss"],
})
export class ManageUserComponent {
  searchParams = {
    email: "",
    firstName: "",
    familyName: "",
    isLocked: false,
  };
  users: ApplicationUserDto[] = [];
  paginatedUsers: ApplicationUserDto[] = [];
  user: ApplicationUserResponse = null;

  // Pagination related properties
  itemsPerPage = 10;
  currentPage = 1;

  constructor(
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService
  ) {}

  ngOnInit() {
    this.searchUser(); // Load initial data
  }

  searchUser() {
    //insert 100 users into the users array
    this.users = [];
    for (let i = 0; i < 100; i++) {
      this.users.push({
        id: i,
        email: "test" + i + "@test.com",
        firstName: "Test",
        familyName: "User",
        accountLocked: i < 50 ? false : true,
      });
    }

    // Uncomment this when using actual API
    /*
    this.userEndpointService
      .findUserByEmail(this.searchParams.email)
      .subscribe({
        next: (response) => {
          this.user = response;
          this.messagingService.setMessage("User loaded successfully.");
          this.updatePagination();
        },
        error: (error) => console.error("Error loading user:", error),
      });
    */
  }

  onTableDataChange(event: any): void {
    this.currentPage = event;
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
