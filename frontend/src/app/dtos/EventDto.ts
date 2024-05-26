import {Event, Show} from "../services/openapi";

export class EventDto{
  id: number;
  title: string;
  description: string;
  eventType: Event.EventTypeEnum;
  duration: number;
  shows: Array<Show>;


  constructor(id: number, title: string, description: string, eventType: Event.EventTypeEnum, duration: number, shows: Array<Show>) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.eventType = eventType;
    this.duration = duration;
    this.shows = shows;
  }
}
