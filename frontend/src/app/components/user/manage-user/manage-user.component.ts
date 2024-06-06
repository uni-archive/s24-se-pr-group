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
    this.userEndpointService.getUser().subscribe({
      next: (currentUser) => {
        if (currentUser == null) {
          console.error("Keine Benutzer geladen.");
          return;
        }

        if (user.id != currentUser.id) {
          if (user.superAdmin) {
            console.error("Dieser Benutzer kann nicht gesperrt werden.");
            this.messagingService.setMessage(
              "Dieser Benutzer kann nicht gesperrt werden.",
              "warning"
            );
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
              },
              error: (error) =>
                console.error("Fehler beim Laden der Benutzer:", error),
            });
        } else {
          this.messagingService.setMessage(
            "Aktualisierung des eigenen Benutzers kann nicht durchgeführt werden.",
            "danger"
          );
        }
      },
      error: (error) =>
        console.error("Fehler beim Laden des Benutzers:", error),
    });
  }
}
