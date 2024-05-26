import {Component, OnInit} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {EventSearch} from "../../../dtos/EventSearchDto";

@Component({
  selector: 'app-searchpage',
  templateUrl: './searchpage.component.html',
  styleUrl: './searchpage.component.scss'
})
export class SearchpageComponent {
  public searchMode = "EVENT";

  constructor() {
  }
}
