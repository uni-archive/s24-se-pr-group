import {Component, OnInit} from '@angular/core';
import {EventService} from "../../../services/event.service";
import {EventSearch} from "../../../dtos/EventSearchDto";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrl: './search-page.component.scss'
})
export class SearchPageComponent implements OnInit {
  public searchMode = "EVENT";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    const q = this.route.snapshot.queryParams;
    const qSearchType = q['search-type'];
    if(qSearchType) {
      this.searchMode = qSearchType;
    }
  }

  setSearchMode(mode: string) {
    this.searchMode = mode;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        "search-type": mode,
      },
      queryParamsHandling: "merge",
    })
  }
}
