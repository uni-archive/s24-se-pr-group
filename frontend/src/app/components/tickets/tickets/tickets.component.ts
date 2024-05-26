import {Component, OnInit} from '@angular/core';
import {TicketsTableComponent} from "../tickets-table/tickets-table.component";
import {
  PrintPurchaseInvoiceButtonComponent
} from "../../print-purchase-invoice-button/print-purchase-invoice-button.component";
import {Observable} from "rxjs";
import {TicketDetailsResponse, TicketEndpointService} from "../../../services/openapi";

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [
    TicketsTableComponent,
    PrintPurchaseInvoiceButtonComponent
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss'
})
export class TicketsComponent implements OnInit {

  constructor(
    private ticketService: TicketEndpointService
  ) {
  }

  ngOnInit(): void {
  }

  loadTickets(): Observable<TicketDetailsResponse[]> {
    return this.ticketService.findForUser();
  }
}
