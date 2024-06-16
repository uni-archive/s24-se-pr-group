import { Component, OnInit } from '@angular/core';
import { ShowEndpointService, TicketEndpointService } from "../../../services/openapi";
import { ActivatedRoute, Router } from "@angular/router";
import { MessagingService } from "../../../services/messaging.service";
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-pick-up-tickets',
  templateUrl: './pick-up-tickets.component.html',
  styleUrls: ['./pick-up-tickets.component.scss'],
  providers: [DatePipe]
})
export class PickUpTicketsComponent implements OnInit {
  filterConfig = { firstName: '', familyName: '' };
  showId: number;
  heading: string;
  showConfirmationDialog: boolean = false;
  showCancelDialog: boolean = false;
  ticketIdToValidate: number;
  ticketIdToCancel: number;
  customerNameToDisplay: string;
  currentCriteria: any;
  currentPage: number;
  currentSize: number;
  refresh: boolean = false;

  constructor(private ticketEndpointService: TicketEndpointService,
              private router: Router,
              private route: ActivatedRoute,
              private showEndpointService: ShowEndpointService,
              private messageService: MessagingService,
              private datePipe: DatePipe) { }

  searchTickets = (criteria: any, page: number, size: number) => {
    this.currentCriteria = criteria;
    this.currentPage = page;
    this.currentSize = size;
    return this.ticketEndpointService.searchTicketsInShow(
      this.showId,
      criteria.firstName,
      criteria.familyName,
      true,
      false,
      page,
      size
    );
  };

  ngOnInit(): void {
    this.showId = +this.route.snapshot.paramMap.get('id');
    this.showEndpointService.getShowById(this.showId).subscribe(show => {
      this.heading = 'Tickets für ' + show.event.title + ' am ' + this.datePipe.transform(show.dateTime, 'dd.MM.yyyy HH:mm');
    });
  }

  getHeading() {
    return this.heading;
  }

  markAsPickedUp(ticket: any) {
    this.ticketIdToValidate = ticket.id;
    this.customerNameToDisplay = `${ticket.order.customer.firstName} ${ticket.order.customer.familyName}`;
    this.showConfirmationDialog = true;
  }

  confirmCancellation(ticket: any) {
    this.ticketIdToCancel = ticket.id;
    this.customerNameToDisplay = `${ticket.order.customer.firstName} ${ticket.order.customer.familyName}`;
    this.showCancelDialog = true;
  }

  onConfirm(result: boolean) {
    this.showConfirmationDialog = false;
    if (result) {
      this.ticketEndpointService.validateTicket(this.ticketIdToValidate).subscribe({
        next: () => {
          this.refresh = !this.refresh;
          this.messageService.setMessage('Ticket validated successfully', 'success');
        },
        error: (err) => {
          this.messageService.setMessage('Could not validate ticket: ' + err.error, 'danger');
        }
      });
    }
  }

  onCancel(result: boolean) {
    this.showCancelDialog = false;
    if (result) {
      this.ticketEndpointService.cancelReservedTicket(this.ticketIdToCancel).subscribe({
        next: () => {
          this.refresh = !this.refresh;
          this.messageService.setMessage('Reservation cancelled successfully', 'success');
        },
        error: (err) => {
          this.messageService.setMessage('Could not cancel reservation: ' + err.error, 'danger');
        }
      });
    }
  }
}
