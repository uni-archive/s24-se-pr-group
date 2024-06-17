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
import { ArtistDto } from './artist-dto';
import { EventDto } from './event-dto';
import { LocationDto } from './location-dto';


export interface ShowDto { 
    id?: number;
    dateTime?: string;
    artistList?: Array<ArtistDto>;
    location?: LocationDto;
    event?: EventDto;
}

