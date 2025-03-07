/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { HttpHeaders }                                       from '@angular/common/http';

import { Observable }                                        from 'rxjs';

import { PageTicketDetailsResponse } from '../model/models';
import { SectorTicketCreationRequest } from '../model/models';
import { TicketCreationRequest } from '../model/models';
import { TicketDetailsResponse } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface TicketEndpointServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * 
     * @param sectorTicketCreationRequest 
     */
    addSectionTicket(sectorTicketCreationRequest: SectorTicketCreationRequest, extraHttpRequestParams?: any): Observable<TicketDetailsResponse>;

    /**
     * 
     * 
     * @param ticketCreationRequest 
     */
    addTicket(ticketCreationRequest: TicketCreationRequest, extraHttpRequestParams?: any): Observable<TicketDetailsResponse>;

    /**
     * 
     * 
     * @param id 
     */
    cancelReservedTicket(id: number, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     * @param id 
     * @param body 
     */
    changeTicketReserved(id: number, body: boolean, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     * @param id 
     */
    findById(id: number, extraHttpRequestParams?: any): Observable<TicketDetailsResponse>;

    /**
     * 
     * 
     */
    findForUser(extraHttpRequestParams?: any): Observable<Array<TicketDetailsResponse>>;

    /**
     * 
     * 
     * @param id 
     */
    removeTicket(id: number, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     * @param id 
     * @param firstName 
     * @param familyName 
     * @param reservedOnly 
     * @param valid 
     * @param page 
     * @param size 
     */
    searchTicketsInShow(id: number, firstName?: string, familyName?: string, reservedOnly?: boolean, valid?: boolean, page?: number, size?: number, extraHttpRequestParams?: any): Observable<PageTicketDetailsResponse>;

    /**
     * 
     * 
     * @param id 
     */
    validateTicket(id: number, extraHttpRequestParams?: any): Observable<{}>;

}
