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
import { AddressResponse } from './address-response';


export interface ApplicationUserResponse { 
    id?: number;
    email?: string;
    firstName?: string;
    familyName?: string;
    phoneNumber?: string;
    accountLocked?: boolean;
    isAdmin?: boolean;
    isSuperAdmin?: boolean;
    address?: AddressResponse;
}

