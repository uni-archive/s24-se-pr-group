import { Component } from "@angular/core";
import { Subject, debounceTime } from "rxjs";
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
    firstName: "",
    familyName: "",
    email: "",
    isLocked: null,
  };
  users: ApplicationUserDto[] = [];
  user: ApplicationUserResponse = null;
  searchChangedObservable = new Subject<void>();

  // Pagination related properties
  itemsPerPage = 10;
  currentPage = 1;

  constructor(
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService
  ) {}

  ngOnInit() {
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({ next: () => this.searchUsers() });
    // only display locked users by default
    this.searchParams.isLocked = true;
    this.searchUsers();
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  searchUsers() {
    if (
      this.searchParams.firstName == null ||
      this.searchParams.firstName === ""
    ) {
      delete this.searchParams.firstName;
    } else if (
      this.searchParams.familyName == null ||
      this.searchParams.familyName === ""
    ) {
      delete this.searchParams.familyName;
    } else if (
      this.searchParams.email == null ||
      this.searchParams.email === ""
    ) {
      delete this.searchParams.email;
    } else if (this.searchParams.isLocked == null) {
      delete this.searchParams.isLocked;
    }
    this.userEndpointService
      .searchUsers(
        this.searchParams.firstName,
        this.searchParams.familyName,
        this.searchParams.email,
        this.searchParams.isLocked
      )
      .subscribe({
        next: (response: ApplicationUserDto[]) => {
          this.users = response;
        },
        error: (error) => console.error("Error loading users:", error),
      });
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
