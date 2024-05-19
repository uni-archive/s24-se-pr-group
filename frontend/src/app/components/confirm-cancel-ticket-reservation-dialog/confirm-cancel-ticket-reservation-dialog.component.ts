import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';
import {TicketDetailsResponse} from "../../services/openapi";

@Component({
  selector: 'app-confirm-cancel-ticket-reservation-dialog',
  standalone: true,
  imports: [],
  templateUrl: './confirm-cancel-ticket-reservation-dialog.component.html',
  styleUrl: './confirm-cancel-ticket-reservation-dialog.component.scss'
})
export class ConfirmCancelTicketReservationDialogComponent implements OnInit {
  @Input({ required: true }) ticket: TicketDetailsResponse;
  @Output() confirm = new EventEmitter<void>();

  @HostBinding('class') cssClass = 'modal fade';

  ngOnInit(): void {
  }
}
