import {Component, Input, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {OrderEndpointService, TicketDetailsResponse, TicketEndpointService} from "../../../services/openapi";
import {NgbDatepicker, NgbPagination, NgbPaginationPages} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {formatPrice} from "../../../../formatters/currencyFormatter";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {PdfService} from "../../../services/pdf.service";
import {
  ConfirmCancelTicketReservationDialogComponent
} from "../confirm-cancel-ticket-reservation-dialog/confirm-cancel-ticket-reservation-dialog.component";
import {MessagingService} from "../../../services/messaging.service";
import {Observable} from "rxjs";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

type TicketFilter = "bought" | "reserved" | "all";
type ShowDateFilter = "previous" | "upcoming" | "specific" | "all";
type TicketLoader = Observable<TicketDetailsResponse[]>;

@Component({
  selector: 'app-tickets-table',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    NgIf,
    NgbDatepicker,
    FormsModule,
    RouterLink,
    ConfirmCancelTicketReservationDialogComponent,
    NgbPagination,
    NgbPaginationPages
  ],
  templateUrl: './tickets-table.component.html',
  styleUrl: './tickets-table.component.scss'
})
export class TicketsTableComponent implements OnInit {
  @Input({ required: true }) ticketLoader: TicketLoader;

  constructor(
    private ticketService: TicketEndpointService,
    private pdfService: PdfService,
    private messagingService: MessagingService
  ) {
  }

  protected tickets: TicketDetailsResponse[] = [];
  protected ticketsFiltered: TicketDetailsResponse[] = [];
  protected ticketsPaged: TicketDetailsResponse[] = [];

  protected ticketFilter: TicketFilter = "all";
  protected eventFilter: string = "";
  protected showDateFilter: ShowDateFilter = "upcoming";
  protected showDateFilterSpecificFrom: string | null = null;
  protected showDateFilterSpecificTo: string | null = null;
  protected page: number = 0;

  private readonly defaultPageSize = 10;


  ngOnInit(): void {
    this.loadTickets();
  }
  private loadTickets(): void {
    this.ticketLoader
      .subscribe({
        next: ts => {
          this.tickets = this.sortTicketsByDateDesc(ts);
          this.ticketsFiltered = this.tickets;
          this.updateFilter();
        },
        error: err => {
          console.log(err)
          this.messagingService.setMessage("Ihre Tickets konnten nicht geladen werden. Bitte versuchen Sie es später erneut.", 'error');
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
    this.updatePaging();
  }

  protected updatePaging(): void {
    const origin = this.page - 1
    const size = this.defaultPageSize;
    this.ticketsPaged = this.ticketsFiltered
      .slice(origin * size, (origin + 1) * size);
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
        if (this.showDateFilterSpecificFrom) {
          res = res && showDate >= new Date(this.showDateFilterSpecificFrom);
        }

        if (this.showDateFilterSpecificTo) {
          res = res && showDate <= new Date(this.showDateFilterSpecificTo);
        }

        return res;
    }
  }

  public cancelTicketReservation(ticketId: number): void {
    this.ticketService.cancelReservedTicket(ticketId)
      .subscribe({
        next: () => {
          this.loadTickets();
          this.messagingService.setMessage("Ihre Ticket-Reservierung wurde erfolgreich storniert.");
        },
        error: err => {
          this.messagingService.setMessage("Ihre Ticket-Reservierung konnte nicht storniert werden.", 'error');
        }
      })
  }

  selectPage(page: string) {
    this.page = parseInt(page, 10) || 1;
    this.updatePaging();
  }

  formatInput(input: HTMLInputElement) {
    input.value = input.value.replace('/[^0-9]/g', '');
  }

  protected readonly formatPrice = formatPrice;
}
