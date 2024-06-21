import {EventResponse} from "../services/openapi";
import EventTypeEnum = EventResponse.EventTypeEnum;

export class EventSearch{
  typ: EventTypeEnum | "ALL";
  dauer: number;
  textSearch: string;

  constructor() {
    this.typ = "ALL";
    this.dauer = null;
    this.textSearch = "";
  }

}
