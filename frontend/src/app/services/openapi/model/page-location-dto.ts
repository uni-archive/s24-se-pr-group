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
import { PageableObject } from './pageable-object';
import { SortObject } from './sort-object';
import { LocationDto } from './location-dto';


export interface PageLocationDto { 
    totalPages?: number;
    totalElements?: number;
    size?: number;
    content?: Array<LocationDto>;
    number?: number;
    sort?: Array<SortObject>;
    numberOfElements?: number;
    first?: boolean;
    last?: boolean;
    pageable?: PageableObject;
    empty?: boolean;
}

