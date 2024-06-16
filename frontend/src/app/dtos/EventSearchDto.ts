import {EventResponse, EventSearchDto} from "../services/openapi";
import EventTypeEnum = EventResponse.EventTypeEnum;

export class EventSearch{
  typ: EventTypeEnum;
  dauer: number;
  textSearch: String;

  constructor() {
    this.typ = null;
    this.dauer = null;
    this.textSearch = "";
  }

}
