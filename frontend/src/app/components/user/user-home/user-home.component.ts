import { Component, EventEmitter, Output } from "@angular/core";
import { AuthService } from "src/app/services/auth.service";
import {
  ApplicationUserResponse,
  UserEndpointService,
} from "src/app/services/openapi";

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
  user: ApplicationUserResponse = {};
  isAdmin: boolean = false;
  showManageUserForm: boolean = false;
  showCreateUserForm: boolean = false;
  showEditUserForm: boolean = false;
  selectedLinkId: string = "";
  @Output() isAdminChange = new EventEmitter<boolean>();

  constructor(
    private authService: AuthService,
    private userEndpointService: UserEndpointService
  ) {}

  toggleView(id: string) {
    this.selectedLinkId = id;
    this.showEditUserForm = false;
    this.showManageUserForm = false;
    this.showCreateUserForm = false;
    this.isAdminChange.emit(false);
    if (id === "editUser") {
      this.showEditUserForm = true;
    }
    if (this.isAdmin) {
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
    this.userEndpointService.getUser().subscribe((user) => {
      this.user = user;
    });
    this.selectedLinkId = "editUser";
    this.showEditUserForm = true;
    if (
      this.authService.isLoggedIn() &&
      this.authService.getUserRole() === Role.admin
    ) {
      this.isAdmin = true;
    }
  }

  public get heading(): string {
    return this.isAdmin ? "Admin Homepage" : "User Homepage";
  }
}
