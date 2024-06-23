import {Injectable} from '@angular/core';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
import {Content, Style, TableCell, TDocumentDefinitions} from "pdfmake/interfaces";
import {
  ApplicationUserResponse,
  EventResponse, HallSeatResponse,
  InvoiceResponse,
  OrderDetailsResponse,
  ShowResponse,
  TicketDetailsResponse,
} from "./openapi";
import {formatDate, formatTime} from "../../formatters/datesFormatter";
import {formatPrice} from "../../formatters/currencyFormatter";

@Injectable({
  providedIn: 'root'
})
export class PdfService {
  private readonly companyName = "Ticket-Line Corp.";
  private readonly companyAddress = "Wiedner Hauptstraße 76/2/2, 1040 Wien";
  private readonly companyTelephoneNumber = "+123 456 78";
  private readonly companyEmail = "ticket-line@company.com";

  // ref: https://www.usp.gv.at/steuern-finanzen/umsatzsteuer/umsatzsteuer-identifikationsnummer.html
  // ref: https://de.wikipedia.org/wiki/Umsatzsteuer-Identifikationsnummer#%C3%96sterreich
  private readonly companyUID = "ATU99999999"

  private readonly headerFontSize: number = 20;
  private readonly subheaderFontSize: number = 16;
  private readonly defaultFontSize: number = 12;
  private readonly subFontSize: number = 12;
  private readonly smallFontSize: number = 10;

  private readonly headerStyle: Style = {
    fontSize: this.headerFontSize,
    bold: true,
  };

  private readonly subheaderStyle: Style = {
    fontSize: this.subheaderFontSize,
    bold: true,
  };
  private readonly tableHeaderStyle: Style = {
    fillColor: "#add8e6",
  };

  private readonly smallFont: Style = {
    fontSize: this.smallFontSize,
    italics: true,
  };

  private readonly ticketTableHeaderStyle: Style = {
    fontSize: this.defaultFontSize,
    bold: true,
  };

  private readonly priceAreaStyle: Style = {};

  private readonly priceValueStyle: Style = {
    bold: true,
  };

  private readonly addressStyle: Style = {
    opacity: 0.8,
    fontSize: this.subFontSize,
  };

  private readonly nameStyle: Style = {
    bold: true,
    fontSize: this.defaultFontSize,
  };

  private readonly ticketTableHeader: TableCell[] = [
    {
      text: 'Ticket-Nr.',
      style: this.tableHeaderStyle,
    },
    {
      text: 'Aufführungs-Datum',
      style: this.tableHeaderStyle,
    },
    {
      text: 'Reserviert',
      style: this.tableHeaderStyle,
    },
    {
      text: 'Gültig',
      style: this.tableHeaderStyle,
    },
    {
      text: 'Veranstaltung',
      style: this.tableHeaderStyle,
    },
    {
      text: 'Preis',
      style: this.tableHeaderStyle,
    },
  ];

  private readonly largeGap: Content = {text: '', marginBottom: 30};
  private readonly mediumGap: Content = {text: '', marginBottom: 20};
  private readonly smallGap: Content = {text: '', marginBottom: 10};


  public createTicketPDF(ticket: TicketDetailsResponse): void {
    const docDefinition: TDocumentDefinitions = {
      pageSize: "A6",
      pageOrientation: "landscape",
      watermark: {text: "Ticket-Line", opacity: 0.1},
      content: [
        {
          alignment: "center",
          columns: [
            {
              stack: [
                {
                  text: 'Ticket-Line - Ticket', style: this.headerStyle, noWrap: true,
                },
                {
                  text: `Ticket Nr: ${ticket.id}`
                },
                this.smallGap,
                this.eventInfoField(ticket.show.event),
                this.smallGap,
                this.showInfoField(ticket.show),
                this.smallGap,
                this.seatInfoField(ticket),
              ],
            },
            this.ticketQRField(ticket),
          ]
        },
      ]
    };

    const pdfName = `ticket-${ticket.show.event.title}-${formatDate(ticket.show.dateTime)}.pdf`;
    pdfMake.createPdf(docDefinition, null, null, pdfFonts.pdfMake.vfs).download(pdfName);
  }

  private eventInfoField(event: EventResponse): Content {
    return {
      stack: [
        {
          text: "Veranstaltung",
          style: this.subheaderStyle,
        },
        {
          text: event.title,
        },
      ]
    };
  }

  private showInfoField(show: ShowResponse): Content {
    return {
      stack: [
        {
          text: "Start",
          style: this.subheaderStyle,
        },
        {
          text: formatDate(show.dateTime)
        },
        {
          text: formatTime(show.dateTime)
        }
      ],
    };
  }

  private seatInfoField(ticket: TicketDetailsResponse): Content {
    const spot = ticket.hallSpot;
    if(this.isHallSeat(spot)) {
      return {
        stack: [
          {
            text: "Sitzplatz",
            style: this.subheaderStyle,
          },
          {
            text: `Sektor: ${ticket.hallSpot.sector.id}, Sitzplatz Nr: ${spot.id}`,
          }
        ],
      };
    } else {
      return {
        stack: [
          {
            text: "Stehplatz",
            style: this.subheaderStyle,
          },
          {
            text: `Sektor: ${ticket.hallSpot.sector.id}`,
          }
        ],
      };
    }
  }

  private isHallSeat(hallSpot: any): hallSpot is HallSeatResponse {
    return hallSpot && hallSpot.frontendCoordinates !== undefined;
  }

  private ticketQRField(ticket: TicketDetailsResponse): Content {
    return {
      stack: [
        {
          qr: `${ticket.id}:${ticket.hash}`,
        },
        { text: `${ticket.hash}`, fontSize: this.smallFontSize * 0.5 }
      ],
      alignment: "center",
      marginTop: 50,
    };
  }

  public createCancellationInvoicePDF(order: OrderDetailsResponse): void {
    const cancellationInvoice = this.findCancellationInvoice(order);
    const docDefinition: TDocumentDefinitions = {
      watermark: {text: "Ticket-Line", opacity: 0.1},
      content: [
        {
          columns: [
            {
              stack: [
                {text: 'Ticket-Line - Stornorechnung', style: this.headerStyle, noWrap: true},
                this.smallGap,
                this.companyDataField(),
                this.smallGap,
                this.customerDataField(order.customer as any),
                this.largeGap,
              ]
            },
            {
              alignment: "right",
              stack: [
                {text: `Rechnung Nr: ${cancellationInvoice.id}`},
                {text: formatDate(cancellationInvoice.dateTime), fontSize: this.defaultFontSize},
              ]
            }
          ]
        },
        this.cancellationInvoiceTextField(order),
        this.ticketsTable(order.tickets),
        this.largeGap,
        this.refundedTicketPriceSummaryField(order),
        this.largeGap,
        this.closingTextField(),
      ]
    };

    pdfMake.createPdf(docDefinition, null, null, pdfFonts.pdfMake.vfs).download('cancellation-invoice.pdf');

  }

  public createPurchaseInvoicePDF(order: OrderDetailsResponse): void {
    const purchaseInvoice = this.findPurchaseInvoice(order);
    const docDefinition: TDocumentDefinitions = {
      watermark: {text: "Ticket-Line", opacity: 0.1},
      content: [
        {
          columns: [
            {
              stack: [
                {text: 'Ticket-Line - Rechnung', style: this.headerStyle, noWrap: true},
                this.smallGap,
                this.companyDataField(),
                this.smallGap,
                this.customerDataField(order.customer as any),
                this.largeGap,
              ]
            },
            {
              alignment: "right",
              stack: [
                {text: `Rechnung Nr: ${purchaseInvoice.id}`},
                {text: formatDate(purchaseInvoice.dateTime), fontSize: this.defaultFontSize},
              ]
            }
          ]
        },
        this.purchaseInvoiceTextField(order),
        this.ticketsTable(order.tickets),
        this.largeGap,
        this.ticketPriceSummaryField(order),
        this.largeGap,
        this.closingTextField(),
      ]
    };

    pdfMake.createPdf(docDefinition, null, null, pdfFonts.pdfMake.vfs).download('purchase-invoice.pdf');
  }

  private customerNameField(user: ApplicationUserResponse): Content {
    return {
      text: this.formatCustomerName(user),
      style: this.nameStyle
    };
  }

  private customerIDField(user: ApplicationUserResponse): Content {
    return {text: `(Kunden-Nr: ${user.id})`};
  }

  private customerNameWithIDField(user: ApplicationUserResponse): Content {
    return {
      alignment: "left",
      columns: [
        this.customerNameField(user),
        this.customerIDField(user),
      ]
    }
  }

  private customerAddressField(user: ApplicationUserResponse & { address: string }): Content {
    return {
      text: user.address,
      style: this.addressStyle
    };
  }

  // TODO: Remove when user gets address
  private customerDataField(user: ApplicationUserResponse & {
    address: string
  }): Content[] {
    return [
      this.customerNameWithIDField(user),
      this.customerAddressField(user)
    ];
  }

  private companyNameField(): Content {
    return {
      text: this.companyName,
      style: this.nameStyle
    };
  }

  private companyAddressField(): Content {
    return {
      text: this.companyAddress,
      style: this.addressStyle
    };
  }

  private companyUIDField(): Content {
    return {
      text: this.companyUID,
      style: this.addressStyle,
    }
  }

  private companyDataField(): Content[] {
    return [
      this.companyNameField(),
      this.companyAddressField(),
      this.companyUIDField(),
    ];
  }

  private formatCustomerName(user: ApplicationUserResponse): string {
    // A user might not have both a firstname and a family name
    // This little snippet joins the names via space if both are given or just returns the only defined one.
    return [user.firstName, user.familyName]
      .filter(Boolean)
      .join(' ');
  }

  private ticketsTableRow(t: TicketDetailsResponse): TableCell[] {
    const fillColor = !t.valid
      ? "red"
      : t.reserved
        ? "blue"
        : "";
    const fillOpacity = 0.4;
    return [
      {text: t.id, alignment: 'left', fillColor, fillOpacity},
      {text: formatDate(t.show.dateTime), alignment: 'left', fillColor, fillOpacity},
      {text: t.reserved ? 'Ja' : 'Nein', alignment: 'left', fillColor, fillOpacity},
      {text: t.valid ? 'Ja' : 'Nein', alignment: 'left', fillColor, fillOpacity},
      {text: t.show.event.title, alignment: 'left', fillColor, fillOpacity},
      {text: formatPrice(t.hallSpot.sector.hallSectorShow.price), alignment: "left", fillColor, fillOpacity},
    ];
  }

  private ticketsTable(tickets: TicketDetailsResponse[]): Content {
    return {
      table: {
        body: [this.ticketTableHeader].concat(tickets.map(t => this.ticketsTableRow(t))),
        headerRows: 1,
        widths: ['auto', '*', 'auto', 'auto', '*', '*']
      }
    };
  }

  private calculateOrderTotalPrice(order: OrderDetailsResponse): number {
    const res = order.tickets
      .filter(t => !t.reserved)
      .map(t => t.hallSpot.sector.hallSectorShow.price)
      .reduce((a, b) => a + b, 0);
    console.log(order.tickets, res);
    return res;
  }

  private ticketPriceBeforeTaxValueField(order: OrderDetailsResponse): Content {
    return {
      text: formatPrice(this.calculateOrderTotalPrice(order)),
      style: this.priceValueStyle,
      width: "auto",
    } as Content
  }

  private ticketPriceAfterTaxValueField(order: OrderDetailsResponse): Content {
    return {
      text: formatPrice(this.calculateOrderTotalPrice(order) * 1.2),
      style: this.priceValueStyle,
      width: "auto",
    } as Content
  }

  private ticketPriceBeforeTaxAreaField(order: OrderDetailsResponse): Content {
    return {
      alignment: "right",
      columns: [
        {
          text: "Summe netto:"
        },
        {
          text: this.ticketPriceBeforeTaxValueField(order),
        }
      ]
    };
  }

  private ticketPriceTaxValueField(order: OrderDetailsResponse): Content {
    return {
      text: formatPrice(this.calculateOrderTotalPrice(order) * 0.2),
      style: this.priceValueStyle,
      width: "auto",
    } as Content
  }

  private ticketPriceTaxField(order: OrderDetailsResponse): Content {
    return {
      alignment: "right",
      columns: [
        {
          text: "zzgl. Umsatzsteuer (20%):"
        },
        {
          text: this.ticketPriceTaxValueField(order),
        }
      ]
    };
  }

  private ticketPriceAfterTaxAreaField(order: OrderDetailsResponse): Content {

    return {
      columns: [
        {text: "Summe brutto: "},
        {text: this.ticketPriceAfterTaxValueField(order)}
      ],
      alignment: "right",
      decoration: "underline",
    };
  }

  private ticketPriceSummaryField(order: OrderDetailsResponse): Content[] {
    return [
      this.ticketPriceBeforeTaxAreaField(order),
      this.smallGap,
      this.ticketPriceTaxField(order),
      this.smallGap,
      this.ticketPriceAfterTaxAreaField(order),
    ]
  }

  private refundedTicketPriceSummaryField(order: OrderDetailsResponse): Content {
    return {
      columns: [
        {text: "Rückerstattet: "},
        {text: this.ticketPriceBeforeTaxValueField(order)}
      ],
      alignment: "right",
      decoration: "underline",
    }
  }

  private cancellationInvoiceTextField(order: OrderDetailsResponse): Content {
    const purchaseInvoice = this.findPurchaseInvoice(order);
    return {
      text: `Es wurde die Rechnung (Rechnung Nr: ${purchaseInvoice.id}) vom ${formatDate(purchaseInvoice.dateTime)} storniert. Die in der Rechnung enthaltenen Tickets, welche dadurch storniert wurden, finden Sie in der folgenden Tabelle:`
    }
  }

  private findPurchaseInvoice(order: OrderDetailsResponse): InvoiceResponse {
    return order.invoices.find(i => i.invoiceType === "PURCHASE")!;
  }

  private findCancellationInvoice(order: OrderDetailsResponse): InvoiceResponse {
    return order.invoices.find(i => i.invoiceType === "CANCELLATION")!;
  }

  private purchaseInvoiceTextField(order: OrderDetailsResponse): Content {
    const purchaseInvoice = this.findPurchaseInvoice(order);
    return {

      text: `Es wurden folgende Tickets am ${formatDate(purchaseInvoice.dateTime)} gekauft:`
    }
  }

  private closingTextField(): Content[] {
    return [
      "Mit freundlichen Grüßen",
      this.smallGap,
      this.companyName,
    ]
  }
  }
