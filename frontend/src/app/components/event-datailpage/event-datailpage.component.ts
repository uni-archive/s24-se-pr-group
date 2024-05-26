import {Component, OnInit} from '@angular/core';
import {EventDto, EventEndpointService, ShowEndpointService, ShowListDto} from "../../services/openapi";
import { ActivatedRoute } from '@angular/router';
import {MessagingService} from "../../services/messaging.service";
import {NgForOf, NgIf} from "@angular/common";
@Component({
  selector: 'app-event-datailpage',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './event-datailpage.component.html',
  styleUrl: './event-datailpage.component.scss'
})
export class EventDatailpageComponent implements OnInit {
  public event : EventDto = null;
  public shows : ShowListDto[] = [];
  public id :number;
  ngOnInit(): void {
    this.id = parseInt(this.route.snapshot.paramMap.get('id'));
    alert("ID IS "+this.id);
    this.eventService.findById3(this.id).subscribe({
      next: value => this.event = value,
      error: err => this.messagingService.setMessage(err)
    });
    this.showService.getShowsByEventId(this.id).subscribe({
      next: value => this.shows = value,
      error: err => this.messagingService.setMessage(err)
    });
  }

  formatDateStr(date : String): String {
    let splitDatetime = date.split("T");
    let splitDate = splitDatetime[0].split("-");
    let splitTime = splitDatetime[1].split(":");
    return  splitDate[2]+"."+splitDate[1]+"."+ splitDate[0] + " " + splitTime[0] + ":" + splitTime[1];
  }
  artistMap(artist) {
    if (artist.artistName) {
      return artist.artistName + " ";
    }else {
      return artist.firstName + " " + artist.lastName+ " ";
    }
  }

  constructor(private eventService : EventEndpointService, private messagingService : MessagingService, private showService : ShowEndpointService, private route : ActivatedRoute) {
  }

}
