import {Component, Input} from '@angular/core';
import {PdfService} from "../../services/pdf.service";
import {OrderEndpointService} from "../../services/openapi";

@Component({
  selector: 'app-print-cancellation-invoice-button',
  standalone: true,
  imports: [],
  templateUrl: './print-cancellation-invoice-button.component.html',
  styleUrl: './print-cancellation-invoice-button.component.scss'
})
export class PrintCancellationInvoiceButtonComponent {
  @Input({required: true}) private orderId: number;

  constructor(
    private pdfService: PdfService,
    private orderService: OrderEndpointService,
  ) {
  }

  public createCancellationInvoicePDF(): void {
    this.orderService.findById(this.orderId)
      .subscribe({
        next: order => {
          this.pdfService.createCancellationInvoicePDF(order);
        },
        error: err => {
          console.log("err cancellation")
        }
      });
  }
}
