import {Action, CalculateScaledPoint, DrawableEntity, InteractableEntity} from "./helpers";
import {HallSeat, HallSection, Point2D} from "./hallplan.component";

export class SectionEntity extends DrawableEntity implements InteractableEntity {
  points: Point2D[];
  isHighlighted = false;
  isSelected = false;
  data: HallSection;
  canEdit = true;

  constructor(section: HallSection, canEdit: boolean) {
    super();
    this.points = section.points;
    this.canEdit = canEdit;
  }

  draw(ctx: CanvasRenderingContext2D, calculateScaledPoint: CalculateScaledPoint) {
    ctx.fillStyle = this.data.color + "66";
    ctx.beginPath();
    this.points.forEach((point, index) => {
      // move to points with respect to scale and pos
      const {x, y} = calculateScaledPoint(point);
      if (index === 0) {
        ctx.moveTo(x, y);
      } else {
        ctx.lineTo(x, y);
      }
    });
    ctx.closePath();
    ctx.fill();
    if (this.isHighlighted) {
      ctx.strokeStyle = 'rgb(255, 255, 255)';
      console.log(this.isSelected)
      if (this.isSelected) {
        ctx.strokeStyle = 'yellow';
      }
      ctx.lineWidth = 2;
      ctx.stroke();
    }
  }

  getActions(): Action[] {
    return [];
  }

  isInside(point: Point2D, ctx: CanvasRenderingContext2D): boolean {
    if (! this.canEdit) {
      return false;
    }
    let isInside = false;
    const x = point.x;
    const y = point.y;

    for (let i = 0, j = this.points.length - 1; i < this.points.length; j = i++) {
      const xi = this.points[i].x;
      const yi = this.points[i].y;
      const xj = this.points[j].x;
      const yj = this.points[j].y;

      const intersect = ((yi > y) !== (yj > y)) &&
        (x < ((xj - xi) * (y - yi)) / (yj - yi) + xi);
      if (intersect) isInside = !isInside;
    }

    return isInside;
  }

  onHighlight(selected: boolean): void {
    this.isHighlighted = true;
    this.isSelected = selected;
    console.log("entite", selected)
  }

  onHighlightEnd(): void {
    this.isHighlighted = false;
    this.isSelected = false;
  }
}

export class SeatEntity extends DrawableEntity implements InteractableEntity {
  pos: Point2D;
  isHighlighted = false;
  isSelected = false;
  data: HallSeat;

  constructor(seat: HallSeat) {
    super();
    this.pos = seat.pos;
  }

  isInside(point: Point2D, ctx: CanvasRenderingContext2D): boolean {
    const {x, y} = this.pos;
    const {x: pointX, y: pointY} = point;
    const distance = Math.sqrt(Math.pow(x - pointX, 2) + Math.pow(y - pointY, 2));
    return distance < 5;
  }

  onHighlight(selected: boolean): void {
    this.isHighlighted = true;
    this.isSelected = selected;
  }

  onHighlightEnd(): void {
    this.isHighlighted = false;
    this.isSelected = false;
  }

  getActions(): Action[] {
    return [];
  }

  draw(ctx: CanvasRenderingContext2D, calculateScaledPoint: CalculateScaledPoint, scale: number) {
    const {x, y} = calculateScaledPoint(this.pos);
    ctx.fillStyle = 'blue';
    ctx.beginPath();
    ctx.arc(x, y, 5 * scale, 0, 2 * Math.PI);
    ctx.fill();
    if (this.isHighlighted) {
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.5)';
      if (this.isSelected) {
        ctx.strokeStyle = 'yellow';
      }
      ctx.lineWidth = 2;
      ctx.stroke();
    }
  }

}

export class BackgroundEntity extends DrawableEntity {
  backgroundImage: HTMLImageElement;
  width: number;
  height: number;

  constructor(backgroundImage: HTMLImageElement, width: number, height: number) {
    super();
    this.backgroundImage = backgroundImage;
    this.width = width;
    this.height = height;
  }

  draw(ctx: CanvasRenderingContext2D, calculateScaledPoint: CalculateScaledPoint, scale: number) {
    const {x, y} = calculateScaledPoint({x: 0, y: 0});
    ctx.drawImage(this.backgroundImage, x, y, this.width * scale, this.height * scale);
  }
}
