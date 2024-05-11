import { Component } from "@angular/core";
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
  showManageUserForm: boolean = true;
  constructor(private authService: AuthService) {}

  selectedLinkId = "manageUser"; // Initial active link ID

  toggleRegistration(id: string) {
    this.selectedLinkId = id;
    this.showManageUserForm = false;
    if (id === "manageUser") {
      this.showManageUserForm = true;
    }
  }

  ngOnInit() {
    this.isAdmin = this.authService.getUserRole() === Role.admin;
  }

  public get heading(): string {
    return this.isAdmin ? "Admin Home Page" : "User Home Page";
  }
}
