import {NgModule} from "@angular/core";
import {mapToCanActivate, RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {LoginComponent} from "./components/login/login.component";
import {AuthGuard} from "./guards/auth.guard";
import {RegistrationComponent} from "./components/user/registration/registration.component";
import {UserHomeComponent} from "./components/user/user-home/user-home.component";
import {TicketsComponent} from "./components/tickets/tickets/tickets.component";
import {OrdersDetailsViewComponent} from "./components/orders/orders-details-view/orders-details-view.component";
import {LocationOverviewComponent} from "./components/location/location-overview/location-overview.component";
import {LocationCreateComponent} from "./components/location/location-create/location-create.component";
import {AdminGuard} from "./guards/admin.guard";
import {LocationEditComponent} from "./components/location/location-edit/location-edit.component";
import {OrdersViewComponent} from "./components/orders/orders-view/orders-view.component";
import {HallplanComponent} from "./components/hallplan/hallplan.component";
import {HallplanCreateComponent} from "./components/hallplan/create/hallplan-create.component";
import {SearchPageComponent} from "./components/searchpage-components/searchpage/search-page.component";
import {CreateEventComponent} from "./components/administrative-tasks/create-event/create-event.component";
import {CreateShowComponent} from "./components/administrative-tasks/create-show/create-show.component";
import {EventDatailpageComponent} from "./components/event-datailpage/event-datailpage.component";
import {NewsComponent} from "./components/news/news.component";
import {NewsDetailComponent} from './components/news/news-detail/news-detail.component';
import {LocationDetailsComponent} from "./components/location/location-details/location-details.component";
import {TicketSelectComponent} from "./components/ticket-select/ticket-select.component";


const routes: Routes = [
  {path: "", component: HomeComponent},
  {path: "register", component: RegistrationComponent},
  {path: "login", component: LoginComponent},
  {
    path: 'news-detail/:id', canActivate: [AuthGuard], component: NewsDetailComponent
  },
  {
    path: 'my', canActivate: mapToCanActivate([AuthGuard]), children:
      [
        {path: 'tickets', component: TicketsComponent},
        {path: 'orders', component: OrdersViewComponent},
        {path: 'orders/:id', component: OrdersDetailsViewComponent}
      ]
  },
  {path: "user/home", component: UserHomeComponent},
  {
    path: "locations", canActivate: mapToCanActivate([AdminGuard]),
    children:
      [
        {path: '', component: LocationOverviewComponent},
        {path: 'create', component: LocationCreateComponent},
        {path: 'edit/:id', component: LocationEditComponent}
      ]
  },
  {
    path: "search",
    children: [
      {path: '', component: SearchPageComponent}
    ]
  },
  {path: "event/:id", component: EventDatailpageComponent},
  {path: "eventcreation", component: CreateEventComponent},
  {path: "showcreation", component: CreateShowComponent},
  {path: 'hallplan', component: HallplanComponent},
  {path: 'hallplan/create', component: HallplanCreateComponent},
  {
    path: "location/:id",
    component: LocationDetailsComponent,
  },
  {path: 'news', canActivate: [AuthGuard], component: NewsComponent},
  {path: "show/:id/ticket-select", component: TicketSelectComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
