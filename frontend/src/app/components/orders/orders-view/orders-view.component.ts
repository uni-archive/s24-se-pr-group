import { Component } from '@angular/core';
import {TicketsTableComponent} from "../../tickets/tickets-table/tickets-table.component";
import {OrderTableComponent} from "../order-table/order-table.component";

@Component({
  selector: 'app-orders-view',
  standalone: true,
  imports: [
    TicketsTableComponent,
    OrderTableComponent,
  ],
  templateUrl: './orders-view.component.html',
  styleUrl: './orders-view.component.scss'
})
export class OrdersViewComponent {

}
