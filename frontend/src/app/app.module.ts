import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { NgxPaginationModule } from "ngx-pagination";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { HeaderComponent } from "./components/header/header.component";
import { FooterComponent } from "./components/footer/footer.component";
import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from "./components/login/login.component";
import { MessageComponent } from "./components/message/message.component";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { httpInterceptorProviders } from "./interceptors";
import { RegistrationComponent } from "./components/user/registration/registration.component";
import { GlobalMessageComponent } from "./global-message/global-message.component";
import { AngularPhoneNumberInput } from "angular-phone-number-input";
import { PrintPurchaseInvoiceButtonComponent } from "./components/print-purchase-invoice-button/print-purchase-invoice-button.component";
import { PrintTicketButtonComponent } from "./components/print-ticket-button/print-ticket-button.component";
import { UserHomeComponent } from "./components/user/user-home/user-home.component";
import { ManageUserComponent } from "./components/user/manage-user/manage-user.component";
import {TicketsTableComponent} from "./components/tickets/tickets-table/tickets-table.component";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    RegistrationComponent,
    GlobalMessageComponent,
    UserHomeComponent,
    ManageUserComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    AngularPhoneNumberInput,
    PrintPurchaseInvoiceButtonComponent,
    PrintTicketButtonComponent,
    TicketsTableComponent,
    NgxPaginationModule
  ],
  providers: [httpInterceptorProviders],
  exports: [HeaderComponent, FooterComponent],
  bootstrap: [AppComponent],
})
export class AppModule {}
