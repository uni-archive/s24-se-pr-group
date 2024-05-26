import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ApplicationUserResponse, UserEndpointService} from '../../services/openapi';
import {Subscription} from 'rxjs';
import {EventService} from "../../services/event.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy, AfterViewInit {
  public applicationUserResponse: ApplicationUserResponse;
  private subscription: Subscription;

  constructor(public authService: AuthService, private userService: UserEndpointService, private eventService: EventService) {
  }

  ngOnInit(): void {
    this.subscription = this.eventService.registrationSuccess$.subscribe(() => {
      this.loadUser();
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  ngAfterViewInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    this.userService.getUser().subscribe({
      next: response => {
        this.applicationUserResponse = response;
      }
    });
  }

  getName(): string {
    if (!this.applicationUserResponse) {
      return '';
    }
    return this.applicationUserResponse.firstName + ' ' + this.applicationUserResponse.familyName;
  }
}
