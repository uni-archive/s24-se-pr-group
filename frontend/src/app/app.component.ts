import {Component, ViewChild} from '@angular/core';
import {ActivationEnd} from "@angular/router";
import {HeaderComponent} from "./components/header/header.component";
import {RegistrationComponent} from "./components/user/registration/registration.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'TicketLine';
}
