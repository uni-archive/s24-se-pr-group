import {Component, OnInit} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {EventSearch} from "../../../dtos/EventSearchDto";

@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrl: './search-page.component.scss'
})
export class SearchPageComponent {
  public searchMode = "EVENT";

  constructor() {
  }
}
