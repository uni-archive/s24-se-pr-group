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
import { NewsComponent } from "./components/news/news.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {httpInterceptorProviders} from "./interceptors";
import {GlobalMessageComponent} from "./global-message/global-message.component";
import {AngularPhoneNumberInput} from "angular-phone-number-input";
import {
  PrintPurchaseInvoiceButtonComponent
} from "./components/print-purchase-invoice-button/print-purchase-invoice-button.component";
import {PrintTicketButtonComponent} from "./components/print-ticket-button/print-ticket-button.component";
import {UserHomeComponent} from "./components/user/user-home/user-home.component";
import {ManageUserComponent} from "./components/user/manage-user/manage-user.component";
import {TicketsTableComponent} from "./components/tickets/tickets-table/tickets-table.component";
import {CountryDropdownComponent} from "./components/user/country-dropdown/country-dropdown.component";
import {LocationOverviewComponent} from "./components/location/location-overview/location-overview.component";
import {LocationCreateComponent} from "./components/location/location-create/location-create.component";
import {LocationEditComponent} from "./components/location/location-edit/location-edit.component";
import {PaginatedListComponent} from "./components/paginated-list/paginated-list.component";
import {LocationSearchComponent} from "./components/location/location-search/location-search.component";
import {UserEditComponent} from "./components/user/user-edit/user-edit.component";
import {EventSearchComponent} from "./components/searchpage-components/event-search/event-search.component";
import {ShowSearchComponent} from "./components/searchpage-components/show-search/show-search.component";
import {RegistrationComponent} from "./components/user/registration/registration.component";
import {SearchPageComponent} from "./components/searchpage-components/searchpage/search-page.component";
import {ArtistSearchComponent} from "./components/searchpage-components/artist-search/artist-search.component";
import {NewsDetailComponent} from "./components/news/news-detail/news-detail.component";
import { NewsCreateComponent } from './components/news/news-create/news-create.component';
import {LocationDetailsComponent } from "./components/location/location-details/location-details.component";
import { LocationAutocompleteComponent } from "./components/location/location-autocomplete/location-autocomplete.component";
import { GenericAutocompleteComponent } from "./components/autocomplete/generic-autocomplete/generic-autocomplete.component";
import { CreateShowComponent } from "./components/administrative-tasks/create-show/create-show.component";
import { AutocompleteTextfieldComponent } from "./components/administrative-tasks/autocomplete-textfield/autocomplete-textfield.component";
import { TagSearchComponent } from "./components/administrative-tasks/tag-search/tag-search.component";
import { ResetPasswordComponent } from "./components/user/reset-password/reset-password.component";
import { ChangePasswordComponent } from "./components/user/change-password/change-password.component";
import { SendResetMailComponent } from "./components/user/reset-password/send-reset-mail/send-reset-mail.component";
import { ActivateAccountComponent } from "./components/user/registration/activate-account/activate-account.component";
import { PickUpTicketsComponent } from "./components/tickets/pick-up-tickets/pick-up-tickets.component";
import { ConfirmationDialogComponent } from "./components/confirmation-dialog/confirmation-dialog.component";
import { PickUpTicketShowSelectComponent } from "./components/tickets/pick-up-ticket-show-select/pick-up-ticket-show-select.component";
import { UserConfirmDeleteDialogComponent } from "./components/user/user-edit/user-confirm-delete-dialog/user-confirm-delete-dialog.component";
import {NgOptimizedImage} from "@angular/common";
import {
  HallPlanAutocompleteComponent
} from "./components/hallplan/hallplan-autocomplete/hall-plan-autocomplete.component";
import {HallplanComponent} from "./components/hallplan/hallplan.component";
import {HallPlanViewerComponent} from "./components/hallplan/hallplan-viewer/hallplan-viewer.component";
import {
    SearchResultCardComponent
} from "./components/searchpage-components/search-result-card/search-result-card.component";
import {ArtistDetailsComponent} from "./components/artist/artist-details/artist-details.component";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    NewsComponent,
    NewsDetailComponent,
    NewsCreateComponent,
    RegistrationComponent,
    GlobalMessageComponent,
    UserHomeComponent,
    ManageUserComponent,
    UserEditComponent,
    CountryDropdownComponent,
    LocationOverviewComponent,
    LocationCreateComponent,
    LocationEditComponent,
    PaginatedListComponent,
    LocationSearchComponent,
    ShowSearchComponent,
    ArtistSearchComponent,
    ArtistDetailsComponent,
    EventSearchComponent,
    GlobalMessageComponent,
    SearchPageComponent,
    LocationDetailsComponent,
    LocationAutocompleteComponent,
    GenericAutocompleteComponent,
    CreateShowComponent,
    AutocompleteTextfieldComponent,
    ResetPasswordComponent,
    ChangePasswordComponent,
    SendResetMailComponent,
    ActivateAccountComponent,
    PickUpTicketsComponent,
    ConfirmationDialogComponent,
    PickUpTicketShowSelectComponent,
    UserConfirmDeleteDialogComponent,
    HallPlanAutocompleteComponent,
    HallPlanViewerComponent
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
        NgxPaginationModule,
        TagSearchComponent,
        NgOptimizedImage,
        HallplanComponent,
        SearchResultCardComponent,
    ],
  providers: [httpInterceptorProviders],
  exports: [
    HeaderComponent,
    FooterComponent,
    LocationSearchComponent,
    LocationAutocompleteComponent,
    PaginatedListComponent,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
