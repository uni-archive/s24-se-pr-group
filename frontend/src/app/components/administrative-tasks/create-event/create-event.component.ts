import { Component } from '@angular/core';
import {EventCreateDto} from "../../../dtos/EventCreateDto";
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {EventCreationDto, EventEndpointService} from "../../../services/openapi";
import {MessagingService} from "../../../services/messaging.service";

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './create-event.component.html',
  styleUrl: './create-event.component.scss'
})
export class CreateEventComponent {

  public createDto: EventCreateDto = new EventCreateDto();
  public eventType: String;

  public eventTypes: String[] = [null, "CONCERT","THEATER", "PLAY"];

  constructor(private service : EventEndpointService, private messagingService: MessagingService) {
  }

  onSubmit() {
    let dto : EventCreationDto = {
      description: this.createDto.description,
      title: this.createDto.title,
      duration: this.createDto.duration*60,
    };
    dto.eventType = (this.eventType=="CONCERT")?
      (EventCreationDto.EventTypeEnum.Concert):
      ((this.eventType=="THEATER")?
        (EventCreationDto.EventTypeEnum.Theater)
        :(EventCreationDto.EventTypeEnum.Play));
      this.service.createEvent(dto).subscribe({
        next: (res) => {
          //TODO: NOTIF
          this.messagingService.setMessage("Veranstaltung erfolgreich erstellt.");
          this.createDto = new EventCreateDto();
          this.eventType = null;
        },
        error: (err) => {
          //TODO: NOTIF
          this.messagingService.setMessage("Veranstaltung konnte nicht erstellt werden.", "warning");
        }
      });


  }
}
