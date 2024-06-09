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

import { NewsResponseDto } from '../model/models';
import { PageNewsResponseDto } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface NewsEndpointServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Publish a new news
     * 
     * @param title 
     * @param summary 
     * @param text 
     * @param image 
     */
    create(title: string, summary: string, text: string, image: Blob, extraHttpRequestParams?: any): Observable<NewsResponseDto>;

    /**
     * Get detailed information about a specific news
     * 
     * @param id 
     */
    find(id: number, extraHttpRequestParams?: any): Observable<NewsResponseDto>;

    /**
     * Get list of news without details
     * 
     * @param page 
     * @param size 
     */
    findAll(page?: number, size?: number, extraHttpRequestParams?: any): Observable<PageNewsResponseDto>;

    /**
     * Get list of unread news
     * 
     * @param page 
     * @param size 
     */
    findUnread(page?: number, size?: number, extraHttpRequestParams?: any): Observable<PageNewsResponseDto>;

}
