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
import { Artist } from './artist';
import { Event } from './event';
import { HallSectorShow } from './hall-sector-show';
import { Location } from './location';


export interface Show { 
    id?: number;
    dateTime?: string;
    artists?: Array<Artist>;
    event?: Event;
    location?: Location;
    hallSectorShows?: Array<HallSectorShow>;
}

