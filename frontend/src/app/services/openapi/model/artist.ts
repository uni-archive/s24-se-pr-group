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
import { Show } from './show';


export interface Artist { 
    id?: number;
    artistName?: string;
    firstName?: string;
    lastName?: string;
    shows?: Array<Show>;
}

