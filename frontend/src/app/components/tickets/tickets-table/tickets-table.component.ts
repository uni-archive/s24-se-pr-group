import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {TicketDetailsResponse, TicketEndpointService} from "../../../services/openapi";
import {NgbDatepicker} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {formatPrice} from "../../../../formatters/currencyFormatter";
import {RouterLink} from "@angular/router";
import {PdfService} from "../../../services/pdf.service";

type TicketFilter = "bought" | "reserved" | "all";
type ShowDateFilter = "previous" | "upcoming" | "specific" | "all";

@Component({
  selector: 'app-tickets-table',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    NgIf,
    NgbDatepicker,
    FormsModule,
    RouterLink
  ],
  templateUrl: './tickets-table.component.html',
  styleUrl: './tickets-table.component.scss'
})
export class TicketsTableComponent implements OnInit {
  constructor(public authService: AuthService, private ticketService: TicketEndpointService, private pdfService: PdfService) { }

  protected tickets: TicketDetailsResponse[] = [];
  protected ticketsFiltered: TicketDetailsResponse[] = [];

  protected ticketFilter: TicketFilter = "all";
  protected eventFilter: string = "";
  protected showDateFilter: ShowDateFilter = "upcoming";
  protected showDateFilterSpecificFrom: string | null = null;
  protected showDateFilterSpecificTo: string | null = null;

  ngOnInit(): void {
    this.ticketService.findForUser()
      .subscribe({
        next: ts => {
          this.tickets = this.sortTicketsByDateDesc(ts);
          this.ticketsFiltered = this.tickets;
          this.updateFilter();
        },
        error: err => {
          console.error(err);
        }
      });
  }

  protected printTicketPDF(ticket: TicketDetailsResponse): void {
    this.pdfService.createTicketPDF(ticket);
  }

  private sortTicketsByDateDesc(tickets: TicketDetailsResponse[]): TicketDetailsResponse[] {
    return tickets.sort((t1, t2) => {
      const d1 = new Date(t1.show.dateTime);
      const d2 = new Date(t2.show.dateTime);
      return d2.getTime() - d1.getTime();
    });
  }

  protected updateFilter(): void {
    this.ticketsFiltered = this.tickets
      .filter(t => this.applyTicketFilter(t))
      .filter(t => this.applyEventFilter(t))
      .filter(t => this.applyShowDateFilter(t));
  }

  private applyTicketFilter(ticket: TicketDetailsResponse): boolean {
    switch (this.ticketFilter) {
      case "bought":
        return !ticket.reserved;
      case "reserved":
        return ticket.reserved;
      case "all":
        return true;
    }
  }

  private applyEventFilter(ticket: TicketDetailsResponse): boolean {
    const regex = new RegExp(this.eventFilter, 'i');
    return regex.test(ticket.show.event.title);
  }

  private applyShowDateFilter(ticket: TicketDetailsResponse): boolean {
    const now = new Date();
    const showDate = new Date(ticket.show.dateTime);
    switch (this.showDateFilter) {
      case "all":
        return true;
      case "previous":
        return showDate < now;
      case "upcoming":
        return showDate > now;
      case "specific":
        let res = true;
        if(this.showDateFilterSpecificFrom) {
          res = res && showDate >= new Date(this.showDateFilterSpecificFrom);
        }

        if(this.showDateFilterSpecificTo) {
          res = res && showDate <= new Date(this.showDateFilterSpecificTo);
        }

        return res;
    }
  }

  protected readonly formatPrice = formatPrice;
}
