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
import { ShowResponse } from './show-response';
import { SortObject } from './sort-object';


export interface PageShowResponse {
    totalElements?: number;
    totalPages?: number;
    totalElements?: number;
    first?: boolean;
    last?: boolean;
    size?: number;
    content?: Array<ShowResponse>;
    number?: number;
    sort?: Array<SortObject>;
    first?: boolean;
    last?: boolean;
    numberOfElements?: number;
    pageable?: PageableObject;
    numberOfElements?: number;
    empty?: boolean;
}

