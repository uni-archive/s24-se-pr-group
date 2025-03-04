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
import { InvoiceResponse } from './invoice-response';
import { ApplicationUserResponse } from './application-user-response';
import { TicketDetailsResponse } from './ticket-details-response';


export interface OrderDetailsResponse { 
    id?: number;
    tickets?: Array<TicketDetailsResponse>;
    customer?: ApplicationUserResponse;
    invoices?: Array<InvoiceResponse>;
    dateTime?: string;
}

