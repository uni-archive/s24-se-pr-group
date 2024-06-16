import { Component, OnInit } from "@angular/core";
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { AuthRequest } from "../../dtos/auth-request";
import { MessagingService } from "../../services/messaging.service";
import { EventService } from "../../services/event.service";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  loginForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = "";

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private eventService: EventService // Inject EventService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required, Validators.minLength(8)]],
    });
    this.route.queryParams.subscribe((params) => {
      this.loginForm.controls["username"].setValue(params["username"]);
    });
  }

  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(
        this.loginForm.controls.username.value,
        this.loginForm.controls.password.value
      );
      this.authenticateUser(authRequest);
    } else {
      console.log("Invalid input");
    }
  }

  authenticateUser(authRequest: AuthRequest) {
    this.authService.loginUser(authRequest).subscribe({
      next: () => {
        console.log("Successfully logged in user: " + authRequest.email);
        this.eventService.emitLoginSuccess(); // Emit login success event
        this.router.navigate(["/"]);
      },
      error: (error) => {
        if (typeof error.error === "object") {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
        this.messagingService.setMessage('Login failed. Incorrect username or password!', 'danger')
      }
    });
  }

  ngOnInit() {}

  navigateToResetPassword() {
    this.router.navigate(["/user/password/send/reset/mail"]);
  }
}
