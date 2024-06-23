import {Component, OnInit} from '@angular/core';
import {TicketsTableComponent} from "../tickets-table/tickets-table.component";
import {
  PrintPurchaseInvoiceButtonComponent
} from "../../print-purchase-invoice-button/print-purchase-invoice-button.component";
import {Observable} from "rxjs";
import {TicketDetailsResponse, TicketEndpointService} from "../../../services/openapi";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [
    TicketsTableComponent,
    PrintPurchaseInvoiceButtonComponent,
    NgIf
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss'
})
export class TicketsComponent implements OnInit {

  tickets: TicketDetailsResponse[] = [];

  constructor(
    private ticketService: TicketEndpointService
  ) {
  }

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.ticketService.findForUser()
      .subscribe({
        next: ts => {
          this.tickets = ts;
        }
      })
  }
}
