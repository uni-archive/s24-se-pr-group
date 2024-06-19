import { Injectable } from "@angular/core";
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Observable } from "rxjs";
import { Globals } from "../global/globals";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private globals: Globals) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const authUri = this.globals.backendUri + "/authentication";
    const registerUri = this.globals.backendUri + "/users/registration";
    let allowedUris = [authUri, registerUri];

    const searchUri = this.globals.backendUri + "/location/search";
    const searchArtistUri = this.globals.backendUri + "/artists/search";
    const searchShowsUri = this.globals.backendUri + "/show/search";
    const eventsSearchUri = this.globals.backendUri + "/events/search";
    const locationUri = this.globals.backendUri + "/location/";
    const showLocationUri = this.globals.backendUri + "/show/location/";
    const byArtistUri = this.globals.backendUri + "/events/by-artist/";
    const showEventUri = this.globals.backendUri + "/show/event/";
    const updatePassword =
      this.globals.backendUri + "/users/user/password/update";
    const sendResetPasswordEmail =
      this.globals.backendUri + "/users/user/password/reset";
    const activateAccountUri =
      this.globals.backendUri + "/users/user/activate/account";
    const newsUri = this.globals.backendUri + '/news/all';
    const newsDetailUri = this.globals.backendUri + '/news/detail/';
    const top10Uri = this.globals.backendUri + '/events/top10';
    let allowedUriStarts = [
      locationUri,
      showLocationUri,
      searchUri,
      searchArtistUri,
      searchShowsUri,
      eventsSearchUri,
      byArtistUri,
      showEventUri,
      updatePassword,
      sendResetPasswordEmail,
      activateAccountUri,
      newsUri,
      newsDetailUri,
      top10Uri
    ];

    console.log("Intercepted request: " + req.url);
    // Do not intercept authentication requests
    if (
      allowedUris.includes(req.url) ||
      (allowedUriStarts.some((uri) => req.url.startsWith(uri)) &&
        !this.authService.isLoggedIn())
    ) {
      console.log("Request is allowed");
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set(
        "Authorization",
        "Bearer " + this.authService.getToken()
      ),
    });

    return next.handle(authReq);
  }
}
