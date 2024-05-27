import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private globals: Globals) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {


    const authUri = this.globals.backendUri + '/authentication';
    const registerUri = this.globals.backendUri + '/users/registration';
    const searchUri = this.globals.backendUri + '/location/search';



    let allowedUris = [authUri, registerUri, searchUri];

    const locationUri = this.globals.backendUri + '/location/';
    const showLocationuri = this.globals.backendUri + '/show/location/';
    let allowedUriStarts = [locationUri, showLocationuri];


    console.log('Intercepted request: ' + req.url)
    // Do not intercept authentication requests
    if (allowedUris.includes(req.url) || allowedUriStarts.some(uri => req.url.startsWith(uri)) && !this.authService.isLoggedIn()) {
      console.log('Request is allowed');
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.authService.getToken())
    });

    return next.handle(authReq);
  }
}
