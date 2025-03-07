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
import { HallPlanDto } from './hall-plan-dto';
import { HallSectorShowDto } from './hall-sector-show-dto';
import { HallSpotDto } from './hall-spot-dto';


export interface HallSectorDto { 
    id?: number;
    hallPlan?: HallPlanDto;
    name?: string;
    frontendCoordinates?: string;
    seats?: Array<HallSpotDto>;
    color?: string;
    hallSectorShow?: HallSectorShowDto;
}

