import {Injectable} from '@angular/core';
import {Globals} from "../global/globals";
import {EventDto} from "./openapi";
import {EventSearch} from "../dtos/EventSearchDto";
import {Observable, Subject} from 'rxjs';
import {EventEndpointService, EventSearchDto} from "./openapi";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private loginSuccessSource = new Subject<void>();
  private registrationSuccessSource = new Subject<void>();

  loginSuccess$ = this.loginSuccessSource.asObservable();
  registrationSuccess$ = this.loginSuccessSource.asObservable();

  emitLoginSuccess() {
    this.loginSuccessSource.next();
  }

  emitRegistrationSuccess() {
    this.registrationSuccessSource.next();
  }

  private messageBaseUri: string = this.globals.backendUri + '/events';

  constructor(private service: EventEndpointService, private globals: Globals) {
  }

  getEvents(searchDto:EventSearch): Observable<EventDto[]> {
    let dto:EventSearchDto = {typ:searchDto.typ, textSearch: searchDto.textSearch.toString(), dauer: searchDto.dauer};
    return this.service.searchEvents(dto);
  }

}
