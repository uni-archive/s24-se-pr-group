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
import { AddressDto } from './address-dto';


export interface ApplicationUserDto { 
    id?: number;
    email?: string;
    password?: string;
    firstName?: string;
    familyName?: string;
    phoneNumber?: string;
    salt?: string;
    loginCount?: number;
    accountLocked?: boolean;
    admin?: boolean;
    superAdmin?: boolean;
    accountActivated?: boolean;
    address?: AddressDto;
}

