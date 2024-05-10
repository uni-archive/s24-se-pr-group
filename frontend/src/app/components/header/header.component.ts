import {AfterViewInit, Component} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ApplicationUserResponse, UserEndpointService} from "../../services/openapi";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements AfterViewInit {
  public applicationUserResponse: ApplicationUserResponse;

  constructor(public authService: AuthService, private userService: UserEndpointService) {

  }

  ngAfterViewInit(): void {
    this.userService.getUser().subscribe({next: response => {
      this.applicationUserResponse = response;
      console.log("response");
      console.log(response);
    }});
  }

  getName(): string {
    if (!this.applicationUserResponse) {
      return '';
    }
    return this.applicationUserResponse.firstName + ' ' + this.applicationUserResponse.familyName;
  }
}
