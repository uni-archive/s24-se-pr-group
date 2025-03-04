import {Component, OnInit, ViewChild} from '@angular/core';
import {OrderDetailsResponse, OrderEndpointService, TicketDetailsResponse} from "../../../services/openapi";
import {ActivatedRoute, Router} from "@angular/router";
import {MessagingService} from "../../../services/messaging.service";
import {TicketsTableComponent} from "../../tickets/tickets-table/tickets-table.component";
import {Subject} from "rxjs";
import {NgIf} from "@angular/common";
import {
  ConfirmCancelTicketReservationDialogComponent
} from "../../tickets/confirm-cancel-ticket-reservation-dialog/confirm-cancel-ticket-reservation-dialog.component";
import {ConfirmCancelOrderDialogComponent} from "../confirm-cancel-order-dialog/confirm-cancel-order-dialog.component";
import {PdfService} from "../../../services/pdf.service";
import {HttpStatusCode} from "@angular/common/http";

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

  @ViewChild("ticketsTableComponent") ticketsTableComponent: TicketsTableComponent;


  order: OrderDetailsResponse;
  loading: boolean = true;
  loadTickets: Subject<TicketDetailsResponse[]> = new Subject<TicketDetailsResponse[]>();
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

  loadOrder(): void {
    const orderId = parseInt(this.route.snapshot.paramMap.get("id"));
    if (isNaN(orderId)) {
      this.messagingService.setMessage("Es wurde eine ungültige Bestellungs-Nr. angegeben.", 'danger');
      this.router.navigate(["/my/orders"]);
    }
    this.orderService.findById1(orderId).subscribe({
      next: order => {
        this.order = order;
        console.log("start refreshing...");
        if(this.ticketsTableComponent) {
          this.ticketsTableComponent.refreshTicketsSorting(this.order.tickets);
        }
        console.log("end refreshing...");
      },
      error: err => {
        this.messagingService.setMessage("Die Bestellung konnte nicht geladen werden.", 'danger');
        this.router.navigate(["/my/orders"]);
      }
    });
  }

  cancelOrder(): void {
   this.orderService.cancelOrder(this.order.id)
      .subscribe({
        next: () => {
          this.messagingService.setMessage("Ihre Bestellung wurde erfolgreich storniert.");
          window.location.reload();
        },
        error: err => {
          if(err.status === HttpStatusCode.UnprocessableEntity) {
            this.messagingService.setMessage("Die Bestellung darf nicht storniert werden: Die Stornierfrist ist abgelaufen.", 'danger')
          } else {
            this.messagingService.setMessage("Konnte die Bestellung nicht stornieren. Bitte versuchen Sie es später erneut.", 'danger');
          }
        }
      });
  }

  printPurchaseReceipt(): void {
    this.pdfService.createPurchaseInvoicePDF(this.order);
  }

  printCancellationReceipt(): void {
    if(this.order.invoices.length < 2) {
      this.messagingService.setMessage("Konnte kein Storno-PDF erstellen. Die Rechnung wurde noch nicht storniert.", 'danger');
      return;
    }

    this.pdfService.createCancellationInvoicePDF(this.order);
  }
}
