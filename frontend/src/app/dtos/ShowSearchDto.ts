
export class ShowSearchDto {
  dateTime: string;
  price: number;
  eventId: number;
  location: number;

  constructor() {
    this.dateTime = null;
    this.eventId = null;
    this.location = null;
    this.price = null;
  }
}
