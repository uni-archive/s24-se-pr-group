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

import { LocationCreateRequest } from '../model/models';
import { LocationDto } from '../model/models';
import { PageLocationDto } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface LocationEndpointServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * 
     * @param id 
     */
    _delete(id: number, extraHttpRequestParams?: any): Observable<{}>;

    /**
     * 
     * 
     * @param locationCreateRequest 
     */
    create1(locationCreateRequest: LocationCreateRequest, extraHttpRequestParams?: any): Observable<LocationDto>;

    /**
     * 
     * 
     */
    findAll1(extraHttpRequestParams?: any): Observable<object>;

    /**
     * 
     * 
     * @param id 
     */
    findById2(id: number, extraHttpRequestParams?: any): Observable<LocationDto>;

    /**
     * 
     * 
     * @param name 
     * @param city 
     * @param street 
     * @param postalCode 
     * @param country 
     * @param page 
     * @param size 
     * @param sort 
     * @param withUpComingShows 
     */
    search1(name?: string, city?: string, street?: string, postalCode?: string, country?: string, page?: number, size?: number, sort?: string, withUpComingShows?: boolean, extraHttpRequestParams?: any): Observable<PageLocationDto>;

    /**
     * 
     * 
     * @param id 
     * @param locationDto 
     */
    update(id: number, locationDto: LocationDto, extraHttpRequestParams?: any): Observable<LocationDto>;

}
