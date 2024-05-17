import { Component, EventEmitter, Output } from "@angular/core";
import { AuthService } from "src/app/services/auth.service";

export enum Role {
  admin = "ADMIN",
  user = "USER",
}

@Component({
  selector: "app-user-home",
  templateUrl: "./user-home.component.html",
  styleUrl: "./user-home.component.scss",
})
export class UserHomeComponent {
  isAdmin: boolean = false;
  showManageUserForm: boolean = false;
  showCreateUserForm: boolean = false;
  selectedLinkId: string = "";
  @Output() isAdminChange = new EventEmitter<boolean>();

  constructor(private authService: AuthService) {}

  toggleRegistration(id: string) {
    if (this.isAdmin) {
      this.selectedLinkId = id;
      this.showManageUserForm = false;
      this.showCreateUserForm = false;
      this.isAdminChange.emit(false);
      if (id === "manageUser") {
        this.showManageUserForm = true;
      }
      if (id === "createUser") {
        this.showCreateUserForm = true;
        this.isAdminChange.emit(true);
      }
    }
  }

  ngOnInit() {
    if (
      this.authService.isLoggedIn() &&
      this.authService.getUserRole() === Role.admin
    ) {
      this.selectedLinkId = "createUser";
      this.showCreateUserForm = true;
      this.isAdmin = true;
    }
  }

  public get heading(): string {
    return this.isAdmin ? "Admin Home Page" : "User Home Page";
  }
}
