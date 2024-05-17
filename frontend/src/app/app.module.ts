import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {RegistrationComponent} from "./components/user/registration/registration.component";
import {GlobalMessageComponent} from "./global-message/global-message.component";
import {AngularPhoneNumberInput} from "angular-phone-number-input";
import {PrintPurchaseInvoiceButtonComponent} from "./components/print-purchase-invoice-button/print-purchase-invoice-button.component";
import {PrintTicketButtonComponent} from "./components/print-ticket-button/print-ticket-button.component";

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
        PrintTicketButtonComponent
    ],
  providers: [httpInterceptorProviders],
  exports: [HeaderComponent, FooterComponent],
  bootstrap: [AppComponent],
})
export class AppModule {}
