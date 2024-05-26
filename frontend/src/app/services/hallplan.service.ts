import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {Message} from "../dtos/message";
import {HallplanCreateDto} from "../dtos/hallplan";

@Injectable({
  providedIn: 'root'
})
export class HallplanService {
  private hallplanBaseUri: string = this.globals.backendUri + '/hallplan';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists hallplan to the backend
   *
   * @param toCreate to persist
   */
  createHallplan(toCreate: HallplanCreateDto): Observable<boolean> {
    console.log('Create hallplan with name ' + toCreate.name);
    return this.httpClient.post<boolean>(this.hallplanBaseUri + "/create", toCreate);
  }
}
