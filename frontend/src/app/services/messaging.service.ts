import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private messageSource = new Subject<{text: string, type: string}>();

  getMessage(): Observable<{text: string, type: string}> {
    return this.messageSource.asObservable();
  }

  setMessage(message: string, type: string = 'success') {
    this.messageSource.next({text: message, type});

    if (type === 'success' || type === 'warning') {
      setTimeout(() => {
        this.clearMessage();
      }, 10000); // 10 seconds
    }
  }

  clearMessage() {
    this.messageSource.next(null);
  }
}
