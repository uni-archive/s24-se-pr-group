import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private registrationSuccessSubject = new Subject<void>();

  registrationSuccess$ = this.registrationSuccessSubject.asObservable();

  emitRegistrationSuccess() {
    this.registrationSuccessSubject.next();
  }
}
