import {HallSeat, HallSection, Point2D} from "./hallplan.component";

export class Helper {
  frameHasChanged = false;
  enabled = true;

  enable() {
    this.enabled = true;
  }

  disable() {
    this.enabled = false;
  }

  resetFrameHasChanged() {
    this.frameHasChanged = false;
  }

  setFrameHasChanged() {
    this.frameHasChanged = true;
  }

  onDrawCanvas(sections: HallSection[], seats: HallSeat[], ctx: CanvasRenderingContext2D) { }

  /**
   * Handle mouse move event
   * @param event
   * @returns false if event chain should continue, true if it should abort
   */
  onMouseMove(event: MouseEvent): boolean {
    return false;
  }
  onMouseDown(event: MouseEvent): boolean {
    return false;
  }
  onMouseUp(event: MouseEvent): boolean {
    return false;
  }
  onMouseScroll(event: WheelEvent): boolean {
    return false;
  }
}

export class DrawHelper extends Helper {
  ctx: CanvasRenderingContext2D;
  scale: number;
  offset: Point2D = {x: 0, y: 0};
  backgroundImage: HTMLImageElement;
  width = 800;
  height = 600;
  showSeats = true;

  constructor(ctx: CanvasRenderingContext2D, backgroundImage: HTMLImageElement, initScale: number = 1) {
    super();
    this.ctx = ctx;
    this.scale = initScale;
    this.backgroundImage = backgroundImage;
  }

  setShowSeats(showSeats: boolean) {
    this.showSeats = showSeats;
  }

  setOffset(pos: Point2D) {
    this.offset = pos;
  }

  getOffset(): Point2D {
    return this.offset;
  }

  setScale(scale: number) {
    this.scale = scale;
  }

  getScale(): number {
    return this.scale;
  }

  getScaledPos(pos: Point2D): Point2D {
    return {x: pos.x * this.scale + this.offset.x, y: pos.y * this.scale + this.offset.y};
  }

  getUnscaledPos(pos: Point2D): Point2D {
    return {x: (pos.x - this.offset.x) / this.scale, y: (pos.y - this.offset.y) / this.scale};
  }

  onDrawCanvas(sections: HallSection[], seats: HallSeat[]) {
    this.drawBackgroundImage();
    this.drawAllSections(sections);
    if (this.showSeats) {
      this.drawAllSeats(seats);
    }
  }

  drawBackgroundImage() {
    this.ctx.drawImage(this.backgroundImage, this.offset.x, this.offset.y, this.width * this.scale, this.height * this.scale);
  }

  drawAllSections(sections: HallSection[]) {
    sections.forEach(section => this.drawSection(section));
  }

  drawSection(section: HallSection) {
    this.ctx.fillStyle = 'red';
    this.ctx.beginPath();
    section.points.forEach((point, index) => {
      // move to points with respect to scale and pos
      const x = point.x * this.scale + this.offset.x;
      const y = point.y * this.scale + this.offset.y;
      if (index === 0) {
        this.ctx.moveTo(x, y);
      } else {
        this.ctx.lineTo(x, y);
      }
    });
    this.ctx.closePath();
    this.ctx.fill();
  }

  drawAllSeats(seats: HallSeat[]) {
    var i = 0;
    seats.forEach(seat => {
      this.drawCircle(seat.pos.x, seat.pos.y);
    });
  }

  drawCircle(x: number, y: number) {
    // idk why but arc messes everything up
    // this.ctx.fillStyle = 'rgba(0, 0, 255, 0.4)';
    // this.ctx.arc(x * this.localScale + this.posX, y * this.localScale + this.offset.y, CreateHelper.SEAT_RADIUS * this.localScale / 2, 0, 2 * Math.PI);
    // this.ctx.fill();
    // this.ctx.closePath();
    this.ctx.fillStyle = 'rgba(0, 0, 255, 0.4)';
    this.ctx.fillRect(x * this.scale + this.offset.x, y * this.scale + this.offset.y, 10 * this.scale, 10 * this.scale);
  }
}

export class MoveHelper extends Helper {
  isDragging = false;
  dragStart: Point2D;
  lastMousePos: Point2D = {x: 0, y: 0};
  drawHelper: DrawHelper;
  canvas: HTMLCanvasElement;

  constructor(drawHelper: DrawHelper, canvas: HTMLCanvasElement) {
    super();
    this.drawHelper = drawHelper;
    this.canvas = canvas;
  }

  onMouseScroll(event: WheelEvent) {
    // Handle mouse scroll event here

    // Example: Zooming in/out based on deltaY value
    const zoomFactor = event.deltaY > 0 ? 1.1 : 0.9; // Adjust as needed
    this.zoomAtPoint(zoomFactor, this.lastMousePos);
    // this.posX = this.clamp(this.posX, 0, this.width * this.localScale - this.width);
    // this.posY = this.clamp(this.posY, 0, this.height * this.localScale - this.height);
    return false;
  }

  zoomAtPoint(zoomFactor: number, point: Point2D) {
    const prevScale = this.drawHelper.getScale();
    const updatedScale = Math.floor(clamp(zoomFactor * prevScale, 1, 10) * 1000) / 1000;
    if (prevScale !== updatedScale) {
      const prevOffset = this.drawHelper.getOffset();
      const deltaX = - (point.x - prevOffset.x) * (zoomFactor - 1);
      const deltaY = - (point.y - prevOffset.y) * (zoomFactor - 1);
      this.drawHelper.setOffset({ x: prevOffset.x + deltaX, y: prevOffset.y + deltaY });
      this.drawHelper.setScale(updatedScale);
      this.setFrameHasChanged();
    }
  }

  onMouseMove(event: MouseEvent) {
    const rect = this.canvas.getBoundingClientRect();
    this.lastMousePos = {x: event.clientX - rect.left, y: event.clientY - rect.top};

    if (this.isDragging) {
      // Calculate the distance moved
      const deltaX = event.clientX - this.dragStart.x;
      const deltaY = event.clientY - this.dragStart.y;

      // Update the canvas position
      const prevOffset = this.drawHelper.getOffset();
      this.drawHelper.setOffset({ x: prevOffset.x + deltaX, y: prevOffset.y + deltaY });

      // Update the drag start position
      this.dragStart = {x: event.clientX, y: event.clientY};

      // Redraw the canvas
      this.setFrameHasChanged();
      return true;
    }
    return false;
  }

  onMouseUp(event: MouseEvent) {
    // Stop dragging
    this.isDragging = false;
    return false;
  }

  onMouseDown(event: MouseEvent) {
    // Start dragging
    this.isDragging = true;
    this.dragStart = {x: event.clientX, y: event.clientY};
    return false;
  }
}

export class CreateHelper extends Helper {
  drawHelper: DrawHelper;
  canvas: HTMLCanvasElement;
  ctx: CanvasRenderingContext2D;
  sectionPoints: Point2D[] = [];
  noDraw = false;
  enabled = false;

  private static SEAT_RADIUS = 10;

  constructor(drawHelper: DrawHelper, canvas: HTMLCanvasElement, sections: HallSection[], seats: HallSeat[], ctx: CanvasRenderingContext2D) {
    super();
    this.drawHelper = drawHelper;
    this.canvas = canvas;
    this.ctx = ctx;
  }

  onDrawCanvas(sections: HallSection[], seats: HallSeat[], ctx: CanvasRenderingContext2D) {
    if (this.noDraw)
      return;
    ctx.fillStyle = 'red';
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 4;
    ctx.beginPath();
    this.sectionPoints.forEach((point, index) => {
      // move to points with respect to scale and pos
      const {x, y} = this.drawHelper.getScaledPos(point);
      if (index === 0) {
        ctx.moveTo(x, y);
      } else {
        ctx.lineTo(x, y);
      }
    });
    ctx.fill();
    ctx.stroke();
  }

  onMouseDown(event: MouseEvent) {
    // Handle mouse down event here
    const rect = this.canvas.getBoundingClientRect();
    const pos = {x: event.clientX - rect.left, y: event.clientY - rect.top};
    const unscaledPos = this.drawHelper.getUnscaledPos(pos);


    this.sectionPoints.push(unscaledPos);

    // Redraw the canvas
    this.setFrameHasChanged();
    return true;
  }

  generateSeats(): Point2D[] {
    const seatPoints: Point2D[] = []
    const { max, min} = this.getMinMaxPoints(this.sectionPoints);
    const padding = 5;

    const sectionPath = new Path2D();
    this.sectionPoints.forEach((point, index) => {
      // move to points with respect to scale and pos
      const x = point.x;
      const y = point.y;
      if (index === 0) {
        sectionPath.moveTo(x, y);
      } else {
        sectionPath.lineTo(x, y);
      }
    });

    for (let x = min.x + CreateHelper.SEAT_RADIUS / 2 + padding; x < max.x - padding - CreateHelper.SEAT_RADIUS / 2; x += CreateHelper.SEAT_RADIUS + padding) {
      for (let y = min.y + CreateHelper.SEAT_RADIUS / 2 + padding; y < max.y - padding - CreateHelper.SEAT_RADIUS / 2; y += CreateHelper.SEAT_RADIUS + padding) {
        // console.log('Seat is inside section?', this.ctx.isPointInPath(sectionPath, x, y), x, y, 'section:', section.name, 'color:', section.color, 'price:', section.price);
        if (this.ctx.isPointInPath(sectionPath, x, y)) {
          seatPoints.push({ x, y });
        }
      }
    }
    sectionPath.closePath()

    return seatPoints;
  }

  disable() {
    super.disable();
    this.sectionPoints = [];
  }

  getSectionPolygon(): Point2D[] {
    return this.sectionPoints;
  }

  private getMinMaxPoints(points: Point2D[]): { min: Point2D, max: Point2D } {
    const xs = points.map(p => p.x);
    const ys = points.map(p => p.y);
    return {
      min: { x: Math.min(...xs), y: Math.min(...ys) },
      max: { x: Math.max(...xs), y: Math.max(...ys) }
    };
  }

}

export function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max);
}
