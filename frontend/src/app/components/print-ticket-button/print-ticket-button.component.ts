import {Component, Input} from '@angular/core';
import {PdfService} from "../../services/pdf.service";
import {TicketDetailsResponse, TicketEndpointService} from "../../services/openapi";

@Component({
  selector: 'app-print-ticket-button',
  standalone: true,
  imports: [],
  templateUrl: './print-ticket-button.component.html',
  styleUrl: './print-ticket-button.component.scss'
})
export class PrintTicketButtonComponent {
  @Input({required: true}) private ticketId: number;

  constructor(
    private pdfService: PdfService,
    private ticketService: TicketEndpointService,
  ) {
  }

  public createTicketPDF(): void {
    this.ticketService.findById(this.ticketId)
      .subscribe({
        next: ticket  => {
          console.log(ticket);
          this.pdfService.createTicketPDF(ticket);
        },
        error: err => {
          console.log("err ticket")
        }
      });
  }
}
