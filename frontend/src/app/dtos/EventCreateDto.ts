import {EventCreationDto} from "../services/openapi";

export class EventCreateDto {
  description: string;
  duration: number;
  title: string;
  eventType: EventCreationDto.EventTypeEnum;

  constructor() {
    this.description = null;
    this.duration = null;
    this.title = null;
    this.eventType = null;
  }
}
