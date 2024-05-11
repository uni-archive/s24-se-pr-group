import { NgModule } from "@angular/core";
import { mapToCanActivate, RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from "./components/login/login.component";
import { AuthGuard } from "./guards/auth.guard";
import { MessageComponent } from "./components/message/message.component";
import { RegistrationComponent } from "./components/user/registration/registration.component";
import { UserHomeComponent } from "./components/user/user-home/user-home.component";

const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "register", component: RegistrationComponent },
  { path: "login", component: LoginComponent },
  {
    path: "message",
    canActivate: mapToCanActivate([AuthGuard]),
    component: MessageComponent,
  },
  { path: "user/home", component: UserHomeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
