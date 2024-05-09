import {HallSeat, HallSection, Point2D} from "./hallplan.component";

const MouseButtonKey = {
  LEFT: 0,
  MIDDLE: 1,
  RIGHT: 2
}

interface ObjectConstructor {
  /**
   * Groups members of an iterable according to the return value of the passed callback.
   * @param items An iterable.
   * @param keySelector A callback which will be invoked for each item in items.
   */
  groupBy<K extends PropertyKey, T>(
    items: Iterable<T>,
    keySelector: (item: T, index: number) => K,
  ): Partial<Record<K, T[]>>;
}

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

  onDrawCanvas(ctx: CanvasRenderingContext2D, entities: DrawableEntity[]) { }

  /**
   * Handle mouse move event
   * @param event
   * @returns false if event chain should continue, true if it should abort
   */
  onMouseMove(event: MouseEvent): boolean {
    return false;
  }

  /**
   * Handle mouse move event
   * @param event
   * @returns false if event chain should continue, true if it should abort
   */
  onMouseDown(event: MouseEvent): boolean {
    return false;
  }

  /**
   * Handle mouse move event
   * @param event
   * @returns false if event chain should continue, true if it should abort
   */
  onMouseUp(event: MouseEvent): boolean {
    return false;
  }

  /**
   * Handle mouse move event
   * @param event
   * @returns false if event chain should continue, true if it should abort
   */
  onMouseScroll(event: WheelEvent): boolean {
    return false;
  }
}

export class DrawHelper extends Helper {
  scale: number;
  offset: Point2D = {x: 0, y: 0};
  showSeats = true;

  constructor(initScale: number = 1) {
    super();
    this.scale = initScale;
  }

  setShowSeats(showSeats: boolean) {
    this.showSeats = showSeats;
  }

  getScaledPos(pos: Point2D): Point2D {
    return {x: pos.x * this.scale + this.offset.x, y: pos.y * this.scale + this.offset.y};
  }

  getUnscaledPos(pos: Point2D): Point2D {
    return {x: (pos.x - this.offset.x) / this.scale, y: (pos.y - this.offset.y) / this.scale};
  }

  onDrawCanvas(ctx: CanvasRenderingContext2D, entities: DrawableEntity[]) {
    // @ts-ignore
    const grouped = Object.groupBy(entities, entity => entity.constructor.name);
    Object.entries(grouped)
      .reduce((acc, [key, value]: [any, DrawableEntity[]]) => {
        acc.push(...value);
        return acc;
      }, [])
      .forEach((entity: DrawableEntity) => entity.draw(ctx, this.getScaledPos.bind(this), this.scale));
  }

}

export class MoveHelper extends Helper {
  isDragging = false;
  dragStart: Point2D;
  lastMousePos: Point2D = {x: 0, y: 0};
  drawHelper: DrawHelper;
  canvas: HTMLCanvasElement;
  width: number;
  height: number;

  readonly minZoom: number;

  constructor(drawHelper: DrawHelper, canvas: HTMLCanvasElement, width: number, height: number) {
    super();
    this.drawHelper = drawHelper;
    this.canvas = canvas;
    this.width = width;
    this.height = height;
    this.minZoom = Math.max(canvas.width / width, canvas.height / height);
    drawHelper.scale = Math.max(this.minZoom, drawHelper.scale);
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
    const prevScale = this.drawHelper.scale;
    const updatedScale = Math.floor(clamp(zoomFactor * prevScale, this.minZoom, 10) * 1000) / 1000;
    if (prevScale !== updatedScale) {
      const prevOffset = this.drawHelper.offset;
      const deltaX = - (point.x - prevOffset.x) * (zoomFactor - 1);
      const deltaY = - (point.y - prevOffset.y) * (zoomFactor - 1);
      this.updateOffset({ x: prevOffset.x + deltaX, y: prevOffset.y + deltaY })
      this.drawHelper.scale = updatedScale;
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
      const prevOffset = this.drawHelper.offset;
      this.updateOffset({ x: prevOffset.x + deltaX, y: prevOffset.y + deltaY });

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

  updateOffset(offset: Point2D) {
    this.drawHelper.offset = {
      x: clamp(offset.x, this.canvas.width - this.width * this.drawHelper.scale, 0),
      y: clamp(offset.y, this.canvas.height - this.height * this.drawHelper.scale, 0)
    }
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

  onDrawCanvas(ctx: CanvasRenderingContext2D) {
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
    this.sectionPoints.forEach((point, index) => {
      // move to points with respect to scale and pos
      const {x, y} = this.drawHelper.getScaledPos(point);
      // draw circle
      ctx.beginPath();
      ctx.arc(x, y, 5, 0, 2 * Math.PI);
      ctx.fillStyle = "rgba(0, 0, 0, 0.5)";
      ctx.fill();
    });
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

export class InteractionHelper extends Helper {
  entities: InteractableEntity[] = [];
  drawHelper: DrawHelper;
  canvas: HTMLCanvasElement;
  highlightedEntities: InteractableEntity[] = [];
  selectedEntities: InteractableEntity[] = [];
  onSelectionChange: ((entities: InteractableEntity[]) => void) | null = null;

  constructor(drawHelper: DrawHelper, canvas: HTMLCanvasElement, entities: InteractableEntity[]) {
    super();
    this.entities = entities;
    this.canvas = canvas;
    this.drawHelper = drawHelper;
  }

  setEntities(entities: InteractableEntity[]) {
    this.entities = entities;
  }

  onMouseMove(event: MouseEvent) {
    const rect = this.canvas.getBoundingClientRect();
    const mousePos: Point2D = {x: event.clientX - rect.left, y: event.clientY - rect.top};
    const mouseWorldPos = this.drawHelper.getUnscaledPos(mousePos);

    const highlightedEntities = this.entities.filter(entity => entity.isInside(mouseWorldPos, this.canvas.getContext('2d')));
    // only highlight the last entity
    highlightedEntities.splice(0, highlightedEntities.length - 1);

    const newHighlightedEntities = highlightedEntities.filter(entity => ! this.selectedEntities.includes(entity) && !this.highlightedEntities.includes(entity));
    const unhighlightedEntities = this.highlightedEntities.filter(entity => ! this.selectedEntities.includes(entity) && ! highlightedEntities.includes(entity));

    newHighlightedEntities.forEach(entity => entity.onHighlight(false));
    unhighlightedEntities.forEach(entity => entity.onHighlightEnd());

    if (newHighlightedEntities.length !== 0 || unhighlightedEntities.length !== 0) {
      this.setFrameHasChanged();
    }
    this.highlightedEntities = highlightedEntities;
    return false;
  }

  onMouseDown(event: MouseEvent) {
    if (event.button !== MouseButtonKey.LEFT) {
      return false;
    }
    const rect = this.canvas.getBoundingClientRect();
    const mousePos: Point2D = {x: event.clientX - rect.left, y: event.clientY - rect.top};
    const mouseWorldPos = this.drawHelper.getUnscaledPos(mousePos);

    const newSelectedEntities = this.entities
      .filter(entity =>
        ! this.selectedEntities.includes(entity) &&
        entity.isInside(mouseWorldPos, this.canvas.getContext('2d')));
    // only select the last entity
    newSelectedEntities.splice(0, newSelectedEntities.length - 1);
    const unselectedEntities = this.selectedEntities.filter(entity => entity.isInside(mouseWorldPos, this.canvas.getContext('2d')));
    newSelectedEntities.forEach(entity => entity.onHighlight(true));
    unselectedEntities.forEach(entity => entity.onHighlightEnd());
    this.selectedEntities = [...newSelectedEntities, ...this.selectedEntities.filter(entity => !unselectedEntities.includes(entity))];

    const hasChanged = newSelectedEntities.length !== 0 || unselectedEntities.length !== 0
    if (hasChanged) {
      this.onSelectionChange?.(this.selectedEntities);
      this.setFrameHasChanged();
    }
    return hasChanged;
  }
}

export function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max);
}

export interface CalculateScaledPoint {
  (point: Point2D): Point2D;
}

export interface InteractableEntity {
  isInside(point: Point2D, ctx: CanvasRenderingContext2D): boolean;
  onHighlight(selected: boolean): void;
  onHighlightEnd(): void;
  getActions(): Action[];
}

export interface Action {
  onInteract(): void;
  name: string;
}

export class DrawableEntity {
  pos: Point2D;
  hidden = false;
  data: any;

  constructor(pos: Point2D = {x: 0, y: 0}) {
    this.pos = pos;
  }

  setData(data: any) {
    this.data = data;
    return this;
  }

  draw(ctx: CanvasRenderingContext2D, calculateViewportPoint: CalculateScaledPoint, scale: number) { }

  isVisible(): boolean {
    return !this.hidden;
  }
}
