import {Component, Input} from '@angular/core';
import {PdfService} from "../../services/pdf.service";
import {OrderEndpointService} from "../../services/openapi";

@Component({
  selector: 'app-print-purchase-invoice-button',
  standalone: true,
  imports: [],
  templateUrl: './print-purchase-invoice-button.component.html',
  styleUrl: './print-purchase-invoice-button.component.scss'
})
export class PrintPurchaseInvoiceButtonComponent {
  @Input({required: true}) private orderId: number;

  constructor(
    private pdfService: PdfService,
    private orderService: OrderEndpointService,
  ) {
  }

  public createPurchaseInvoicePDF(): void {
    this.orderService.findById1(this.orderId)
      .subscribe({
        next: order => {
          this.pdfService.createPurchaseInvoicePDF(order);
        },
        error: err => {
          console.log("err purchase", err)
        }
      });
  }
}
