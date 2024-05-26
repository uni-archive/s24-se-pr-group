import {Component, OnInit} from '@angular/core';
import {OrderDetailsResponse, OrderEndpointService, TicketDetailsResponse} from "../../../services/openapi";
import {ActivatedRoute, Router} from "@angular/router";
import {MessagingService} from "../../../services/messaging.service";
import {TicketsTableComponent} from "../../tickets/tickets-table/tickets-table.component";
import {lastValueFrom, Observable, Subject, } from "rxjs";
import {NgIf} from "@angular/common";
import {
  ConfirmCancelTicketReservationDialogComponent
} from "../../tickets/confirm-cancel-ticket-reservation-dialog/confirm-cancel-ticket-reservation-dialog.component";
import {ConfirmCancelOrderDialogComponent} from "../confirm-cancel-order-dialog/confirm-cancel-order-dialog.component";
import {PdfService} from "../../../services/pdf.service";

@Component({
  selector: 'app-orders-details-view',
  standalone: true,
  imports: [
    TicketsTableComponent,
    NgIf,
    ConfirmCancelTicketReservationDialogComponent,
    ConfirmCancelOrderDialogComponent
  ],
  templateUrl: './orders-details-view.component.html',
  styleUrl: './orders-details-view.component.scss'
})
export class OrdersDetailsViewComponent implements OnInit {
  order: OrderDetailsResponse;
  subj: Subject<TicketDetailsResponse[]> = new Subject<TicketDetailsResponse[]>();
  constructor(
    private orderService: OrderEndpointService,
    private route: ActivatedRoute,
    private router: Router,
    private messagingService: MessagingService,
    private pdfService: PdfService,
  ) {
  }

  ngOnInit(): void {
    this.loadOrder()
  }

  private loadOrder(): void {
    const orderId = parseInt(this.route.snapshot.paramMap.get("id"));
    if (isNaN(orderId)) {
      this.messagingService.setMessage("Es wurde eine ungültige Bestellungs-Nr. angegeben.", 'error');
      this.router.navigate(["/my/orders"]);
    }
    this.orderService.findById1(orderId).subscribe({
      next: order => {
        this.order = order;
        this.subj.next(order.tickets);
      },
      error: err => {
        this.messagingService.setMessage("Die Bestellung konnte nicht geladen werden.", 'error');
        this.router.navigate(["/my/orders"]);
      }
    });
  }

  loadTickets(): Observable<TicketDetailsResponse[]> {
    return this.subj;
  }

  cancelOrder(): void {
   this.orderService.cancelOrder(this.order.id)
      .subscribe({
        next: () => {
          this.messagingService.setMessage("Ihre Bestellung wurde erfolgreich storniert.");
          this.loadOrder();
        },
        error: err => {
          this.messagingService.setMessage("Konnte die Bestellung nicht stornieren. Bitte versuchen Sie es später erneut.", 'error');
        }
      })
  }

  printPurchaseReceipt(): void {
    this.pdfService.createPurchaseInvoicePDF(this.order);
  }

  printCancellationReceipt(): void {
    if(this.order.invoices.length < 2) {
      this.messagingService.setMessage("Konnte kein Storno-PDF erstellen. Die Rechnung wurde noch nicht storniert.", 'error');
      return;
    }

    this.pdfService.createCancellationInvoicePDF(this.order);
  }
}
