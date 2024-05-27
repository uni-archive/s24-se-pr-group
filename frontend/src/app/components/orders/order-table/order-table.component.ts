import {Component, OnInit} from '@angular/core';
import {
    ConfirmCancelTicketReservationDialogComponent
} from "../../tickets/confirm-cancel-ticket-reservation-dialog/confirm-cancel-ticket-reservation-dialog.component";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbPagination, NgbPaginationPages} from "@ng-bootstrap/ng-bootstrap";
import {
  OrderEndpointService,
  OrderSummaryResponse
} from "../../../services/openapi";
import {PdfService} from "../../../services/pdf.service";
import {MessagingService} from "../../../services/messaging.service";
import {formatDate} from "../../../../formatters/datesFormatter";
import {formatPrice} from "../../../../formatters/currencyFormatter";
import {RouterLink} from "@angular/router";
import {ConfirmCancelOrderDialogComponent} from "../confirm-cancel-order-dialog/confirm-cancel-order-dialog.component";

@Component({
  selector: 'app-order-table',
  standalone: true,
  imports: [
    ConfirmCancelTicketReservationDialogComponent,
    DatePipe,
    FormsModule,
    NgForOf,
    NgIf,
    NgbPagination,
    NgbPaginationPages,
    RouterLink,
    ConfirmCancelOrderDialogComponent
  ],
  templateUrl: './order-table.component.html',
  styleUrl: './order-table.component.scss'
})
export class OrderTableComponent implements OnInit {

  constructor(
    private orderService: OrderEndpointService,
    private pdfService: PdfService,
    private messagingService: MessagingService
  ) {
  }

  protected orders: OrderSummaryResponse[] = [];
  protected ordersFiltered: OrderSummaryResponse[] = [];
  protected ordersPaged: OrderSummaryResponse[] = [];

  protected page: number = 0;
  private readonly defaultPageSize = 10;


  ngOnInit(): void {
    this.loadOrders();
  }
  private loadOrders(): void {
    this.orderService.findForUser1()
      .subscribe({
        next: os => {
          this.orders = this.sortOrdersByDateDesc(os);
          this.ordersFiltered = this.orders;
          this.updateFilter();
        },
        error: err => {
          console.log(err)
          this.messagingService.setMessage("Ihre Tickets konnten nicht geladen werden. Bitte versuchen Sie es später erneut. ", 'danger');
        }
      });
  }

  isCancelled(order: OrderSummaryResponse): boolean {
    return order.invoices.length > 1;
  }

  protected printTicketPDF(ticket: OrderSummaryResponse): void {
    this.pdfService.createTicketPDF(ticket);
  }

  private sortOrdersByDateDesc(orders: OrderSummaryResponse[]): OrderSummaryResponse[] {
    return orders.sort((o1, o2) => {
      const d1 = new Date(o1.dateTime);
      const d2 = new Date(o2.dateTime);
      return d2.getTime() - d1.getTime();
    });
  }

  protected updateFilter(): void {
    this.ordersFiltered = this.orders;
    this.updatePaging();
  }

  protected updatePaging(): void {
    const origin = this.page - 1
    const size = this.defaultPageSize;
    this.ordersPaged = this.ordersFiltered
      .slice(origin * size, (origin + 1) * size);
  }

  selectPage(page: string) {
    this.page = parseInt(page, 10) || 1;
    this.updatePaging();
  }

  formatInput(input: HTMLInputElement) {
    input.value = input.value.replace('/[^0-9]/g', '');
  }

  printCancellationReceipt(order: OrderSummaryResponse): void {
    this.orderService.findById1(order.id)
      .subscribe({
        next: o => {
          this.pdfService.createCancellationInvoicePDF(o);
        },
        error: err => {
          this.messagingService.setMessage("Konnte Storno-Rechnung nicht drucken. Versuchen Sie es später bitte erneut.", 'danger');
        }
      })
  }

  printPurchaseReceipt(order: OrderSummaryResponse): void {
    this.orderService.findById1(order.id)
      .subscribe({
        next: o => {
          this.pdfService.createPurchaseInvoicePDF(o);
        },
        error: err => {
          this.messagingService.setMessage("Konnte Bestell-Rechnung nicht drucken. Versuchen Sie es später bitte erneut.", 'error');
        }
      })
  }

  protected readonly formatDate = formatDate;
  protected readonly formatPrice = formatPrice;
}
