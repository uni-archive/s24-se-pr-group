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


export interface EventCreationDto { 
    description?: string;
    duration?: number;
    title?: string;
    eventType?: EventCreationDto.EventTypeEnum;
}
export namespace EventCreationDto {
    export type EventTypeEnum = 'CONCERT' | 'THEATER' | 'PLAY';
    export const EventTypeEnum = {
        Concert: 'CONCERT' as EventTypeEnum,
        Theater: 'THEATER' as EventTypeEnum,
        Play: 'PLAY' as EventTypeEnum
    };
}


