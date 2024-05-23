import { NgModule } from "@angular/core";
import { mapToCanActivate, RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from "./components/login/login.component";
import { AuthGuard } from "./guards/auth.guard";
import { MessageComponent } from "./components/message/message.component";
import { RegistrationComponent } from "./components/user/registration/registration.component";
import { UserHomeComponent } from "./components/user/user-home/user-home.component";
import { TicketsComponent } from "./components/tickets/tickets/tickets.component";
import {OrdersDetailsViewComponent} from "./components/orders/orders-details-view/orders-details-view.component";
import {LocationOverviewComponent} from "./components/location/location-overview/location-overview.component";
import {LocationCreateComponent} from "./components/location/location-create/location-create.component";
import {AdminGuard} from "./guards/admin.guard";
import {LocationEditComponent} from "./components/location/location-edit/location-edit.component";
import {OrdersViewComponent} from "./components/orders/orders-view/orders-view.component";



const routes: Routes = [
  {path: "", component: HomeComponent},
  {path: "register", component: RegistrationComponent},
  {path: "login", component: LoginComponent},
  {
    path: "message",
    canActivate: mapToCanActivate([AuthGuard]),
    component: MessageComponent,
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
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
