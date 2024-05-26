
export class ShowCreateDto {
  dateTime: string;
  eventId: number;
  locationId: number;

  constructor() {
    this.dateTime = null;
    this.eventId = null;
    this.locationId = null;
  }
}
