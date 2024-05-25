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

import { AddressCreateRequest } from '../model/models';
import { AddressDto } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface AddressEndpointServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * 
     * @param addressCreateRequest 
     */
    create2(addressCreateRequest: AddressCreateRequest, extraHttpRequestParams?: any): Observable<AddressDto>;

    /**
     * 
     * 
     * @param id 
     */
    delete1(id: number, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     */
    findAll2(extraHttpRequestParams?: any): Observable<Array<AddressDto>>;

    /**
     * 
     * 
     * @param id 
     */
    findById3(id: number, extraHttpRequestParams?: any): Observable<AddressDto>;

    /**
     * 
     * 
     * @param id 
     * @param addressDto 
     */
    update1(id: number, addressDto: AddressDto, extraHttpRequestParams?: any): Observable<AddressDto>;

}
