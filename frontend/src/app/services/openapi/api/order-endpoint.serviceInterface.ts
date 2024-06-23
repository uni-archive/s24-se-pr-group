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

import { OrderDetailsResponse } from '../model/models';
import { PageOrderSummaryResponse } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface OrderEndpointServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * 
     * @param id 
     */
    cancelOrder(id: number, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     */
    createOrder(extraHttpRequestParams?: any): Observable<OrderDetailsResponse>;

    /**
     * 
     * 
     * @param id 
     */
    findById1(id: number, extraHttpRequestParams?: any): Observable<OrderDetailsResponse>;

    /**
     * 
     * 
     * @param page 
     * @param size 
     */
    findForUser1(page?: number, size?: number, extraHttpRequestParams?: any): Observable<PageOrderSummaryResponse>;

    /**
     * 
     * 
     */
    getCurrentOrder(extraHttpRequestParams?: any): Observable<OrderDetailsResponse>;

    /**
     * 
     * 
     * @param id 
     */
    purchaseOrder(id: number, extraHttpRequestParams?: any): Observable<{}>;

}
