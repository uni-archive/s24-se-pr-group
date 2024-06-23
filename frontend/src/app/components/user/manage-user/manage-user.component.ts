import { Component } from "@angular/core";
import { MessagingService } from "src/app/services/messaging.service";
import {
  ApplicationUserDto,
  UserEndpointService,
} from "src/app/services/openapi";

@Component({
  selector: "app-manage-user",
  templateUrl: "./manage-user.component.html",
  styleUrls: ["./manage-user.component.scss"],
})
export class ManageUserComponent {
  isLoading: boolean = false;
  filterConfig = {
    firstName: "",
    familyName: "",
    email: "",
    isLocked: false,
  };
  constructor(
    private userEndpointService: UserEndpointService,
    private messagingService: MessagingService
  ) {}

  ngOnInit() {}

  searchUsers = (criteria: any, page: number, size: number) => {
    return this.userEndpointService.searchUsers(
      criteria.firstName,
      criteria.familyName,
      criteria.email,
      criteria.isLocked,
      page,
      size
    );
  };

  shouldRefresh: boolean;

  triggerRefresh(): void {
    this.shouldRefresh = !this.shouldRefresh;
  }

  updateLockStatus(user: ApplicationUserDto, status: boolean) {
    this.isLoading = true;
    this.userEndpointService.getUser().subscribe({
      next: (currentUser) => {
        if (currentUser == null) {
          console.error("Keine Benutzer geladen.");
          this.isLoading = false;
          return;
        }

        if (user.id != currentUser.id) {
          if (user.superAdmin) {
            console.error("Dieser Benutzer kann nicht gesperrt werden.");
            this.messagingService.setMessage(
              "Dieser Benutzer kann nicht gesperrt werden.",
              "warning"
            );
            this.isLoading = false;
            return;
          }

          let updatedUser = user;
          updatedUser.accountLocked = status;
          this.userEndpointService
            .updateUserStatusByEmail(updatedUser)
            .subscribe({
              next: (response) => {
                user = response;
                this.messagingService.setMessage(
                  `Benutzer ${user.firstName} wurde ${
                    user.accountLocked ? "gesperrt" : "entsperrt"
                  }.`
                );
                this.isLoading = false;
              },
              error: (error) => {
                console.error("Fehler beim Laden der Benutzer:", error);
                this.isLoading = false;
              },
            });
        } else {
          this.messagingService.setMessage(
            "Aktualisierung des eigenen Benutzers kann nicht durchgeführt werden.",
            "danger"
          );
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error("Fehler beim Laden des Benutzers:", error);
        this.isLoading = false;
      },
    });
  }

  resetPassword(user: ApplicationUserDto) {
    this.isLoading = true;
    this.userEndpointService.sendEmailForPasswordReset(user.email).subscribe({
      next: (response) => {
        this.messagingService.setMessage(response.message, "success");
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Fehler beim Laden der Benutzer:", error);
        this.isLoading = false;
      },
    });
  }
}
