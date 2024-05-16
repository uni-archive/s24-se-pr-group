import {Component, OnInit} from '@angular/core';
import {TicketsTableComponent} from "../tickets-table/tickets-table.component";

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [
    TicketsTableComponent
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss'
})
export class TicketsComponent implements OnInit {
  ngOnInit(): void {
  }

}
