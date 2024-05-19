import {Component, OnInit} from '@angular/core';
import {TicketsTableComponent} from "../tickets-table/tickets-table.component";
import {
  PrintPurchaseInvoiceButtonComponent
} from "../../print-purchase-invoice-button/print-purchase-invoice-button.component";

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
  ngOnInit(): void {
  }

}
