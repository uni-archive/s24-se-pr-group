export * from './address-endpoint.service';
import { AddressEndpointService } from './address-endpoint.service';
export * from './address-endpoint.serviceInterface';
export * from './custom-health-endpoint.service';
import { CustomHealthEndpointService } from './custom-health-endpoint.service';
export * from './custom-health-endpoint.serviceInterface';
export * from './location-endpoint.service';
import { LocationEndpointService } from './location-endpoint.service';
export * from './location-endpoint.serviceInterface';
export * from './login-endpoint.service';
import { LoginEndpointService } from './login-endpoint.service';
export * from './login-endpoint.serviceInterface';
export * from './message-endpoint.service';
import { MessageEndpointService } from './message-endpoint.service';
export * from './message-endpoint.serviceInterface';
export * from './order-endpoint.service';
import { OrderEndpointService } from './order-endpoint.service';
export * from './order-endpoint.serviceInterface';
export * from './ticket-endpoint.service';
import { TicketEndpointService } from './ticket-endpoint.service';
export * from './ticket-endpoint.serviceInterface';
export * from './user-endpoint.service';
import { UserEndpointService } from './user-endpoint.service';
export * from './user-endpoint.serviceInterface';
export const APIS = [AddressEndpointService, CustomHealthEndpointService, LocationEndpointService, LoginEndpointService, MessageEndpointService, OrderEndpointService, TicketEndpointService, UserEndpointService];
