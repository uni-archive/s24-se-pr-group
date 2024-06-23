import {Component, OnInit} from '@angular/core';
import {
  ApplicationUserResponse, EventResponse,
  OrderDetailsResponse,
  OrderEndpointService,
  TicketDetailsResponse,
  UserEndpointService
} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";
import {Router} from "@angular/router";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {CreditCardExpiryInputComponent} from "../credit-card-expiry-input/credit-card-expiry-input.component";
import {HttpStatusCode} from "@angular/common/http";
import {formatPrice} from "../../../../formatters/currencyFormatter";

@Component({
  selector: 'app-user-checkout',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    CreditCardExpiryInputComponent,
    NgForOf,
    KeyValuePipe
  ],
  templateUrl: './user-checkout.component.html',
  styleUrl: './user-checkout.component.scss'
})
export class UserCheckoutComponent implements OnInit {
  user: ApplicationUserResponse | null = null;
  order: OrderDetailsResponse | null = null;
  formattedExpiration: string = "";
  monthExpiry: string = "";
  yearExpiry: string = "";
  creditCardForm: FormGroup;
  ticketsByEvent: Map<number, TicketDetailsResponse[]> = new Map<number, TicketDetailsResponse[]>();

  // https://stackoverflow.com/questions/9315647/regex-credit-card-number-tests
  readonly mastercardRegex: RegExp = /^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1][0-9]{13}|720[0-9]{12}))$/g;
  readonly visacardRegex: RegExp = /^4[0-9]{12}(?:[0-9]{3})?$/g;

  readonly creditCardRegex: RegExp = /^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\d{3})\d{11})$/;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserEndpointService,
    private orderService: OrderEndpointService,
    private messagingService: MessagingService,
    private router: Router,
  ) {
    this.creditCardForm = this.formBuilder.group({
      name: ['', Validators.required],
      creditCardNumber: ['', [Validators.required, this.creditCardNumberValidator()]],
      expiry: ['', [Validators.required]],
      cvv: ['',
        [
          Validators.required,
          Validators.pattern(/[0-9]{3}/)
        ]
      ]
    });
  }

  eventById(eventId: number): EventResponse {
    return this.ticketsByEvent.get(eventId)[0].show.event;
  }

  total(tickets: TicketDetailsResponse[]): number {
    return tickets
      .filter(t => !t.reserved)
      .map(t => t.hallSpot.sector.hallSectorShow.price)
      .reduce((p1, p2) => p1 + p2, 0);
  }

  creditCardNumberValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }

      if (typeof value === "string") {
        const stripped = value.replace(/\s+/g, "");
        const isValid = this.creditCardRegex.test(stripped);
        return !isValid
          ? {invalidCreditCardNumber: true}
          : null;
      } else {
        return null;
      }
    }
  }

  markInvalid(ctrl: string): boolean {
    return this.creditCardForm.get(ctrl).invalid && this.creditCardForm.get(ctrl).touched;
  }

  markValid(ctrl: string): boolean {
    return this.creditCardForm.get(ctrl).valid && this.creditCardForm.get(ctrl).touched;
  }

  ngOnInit(): void {
    this.userService.getUser().subscribe({
      next: user => {
        this.user = user;
      },
      error: err => {
        console.log(err);
      }
    });

    this.orderService.getCurrentOrder().subscribe({
      next: order => {
        this.order = order;
        this.order.tickets.forEach(ticket => {
          const event = ticket.show.event;
          if (this.ticketsByEvent.has(event.id)) {
            this.ticketsByEvent.get(event.id).push(ticket);
          } else {
            this.ticketsByEvent.set(event.id, [ticket]);
          }
        });
      },
      error: err => {
        if (err.status === HttpStatusCode.BadRequest) {
          this.messagingService.setMessage("Du hast noch nichts in deinem Warenkorb liegen! Wähle zuerst ein paar Tickets aus um diese dann hier zu kaufen!", 'warning')
          this.router.navigate(["/search"]);
        }
      }
    })
  }


  purchaseOrder(): void {
    if (this.total(this.order.tickets) === 0 || this.creditCardForm.valid) {
      this.orderService.purchaseOrder(this.order.id).subscribe({
        next: () => {
          this.messagingService.setMessage("Die Bestellung war erfolgreich!");
          document.cookie = 'order=; Path=/api/v1; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
          this.router.navigate(["/"]);
        },
        error: err => {
          console.log(err);
        }
      });
    }
  }

  purchaseOrderText(): string {
    if(this.total(this.order.tickets) === 0) {
      return "Reservieren!";
    } else {
      return "Bestellen!";
    }
  }

  updateFormattedExpiration(event: KeyboardEvent): void {
    event.preventDefault();
    const key = event.key;
    if (key === "Backspace") {
      // this.formattedExpiration.substring()
    }

    const digit = +key;
    if (isNaN(digit)) {
      return;
    }
    this.formattedExpiration = event.key;
  }


  protected readonly formatPrice = formatPrice;
}
