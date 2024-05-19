import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';
import {OrderDetailsResponse, TicketDetailsResponse} from "../../../services/openapi";
import {formatPrice} from "../../../../formatters/currencyFormatter";

@Component({
  selector: 'app-confirm-cancel-order-dialog',
  standalone: true,
  imports: [],
  templateUrl: './confirm-cancel-order-dialog.component.html',
  styleUrl: './confirm-cancel-order-dialog.component.scss'
})
export class ConfirmCancelOrderDialogComponent implements OnInit {
  @Input({ required: true }) order: OrderDetailsResponse;
  @Output() confirm = new EventEmitter<void>();

  @HostBinding('class') cssClass = 'modal fade';

  orderPrice(): number {
    return this.order.tickets
      .map(t => t.hallSpot.sector.hallSectorShow.price)
      .reduce((p1, p2) => p1 + p2);
  }

  ngOnInit(): void {
  }

  protected readonly formatPrice = formatPrice;
}
