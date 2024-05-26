import { Component } from "@angular/core";
import { debounceTime, Subject } from "rxjs";
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
  searchParams = {
    firstName: "",
    familyName: "",
    email: "",
    isLocked: false,
  };
  users: ApplicationUserDto[] = [];
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
    }
    if (
      this.searchParams.familyName == null ||
      this.searchParams.familyName === ""
    ) {
      delete this.searchParams.familyName;
    }
    if (this.searchParams.email == null || this.searchParams.email === "") {
      delete this.searchParams.email;
    }
    if (this.searchParams.isLocked == null) {
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

  updateLockStatus(user: ApplicationUserDto, status: boolean) {
    this.userEndpointService.getUser().subscribe({
      next: (currentUser) => {
        if (currentUser == null) {
          console.error("Keine Benutzer geladen.");
          return;
        }

        if (user.id != currentUser.id) {
          if (user.superAdmin) {
            this.messagingService.setMessage(
              "Super admin kann nicht aktualisiert werden.",
              "danger"
            );
            return;
          }

          user.accountLocked = status;
          this.userEndpointService.updateUserStatusByEmail(user).subscribe({
            next: (response) => {
              user = response;
              this.messagingService.setMessage(
                `Benutzer ${user.firstName} wurde ${
                  user.accountLocked ? "gesperrt" : "entsperrt"
                }.`
              );
              this.searchUsers();
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
      error: (error) => console.error("Fehler beim Laden der Benutzer:", error),
    });
  }
}
