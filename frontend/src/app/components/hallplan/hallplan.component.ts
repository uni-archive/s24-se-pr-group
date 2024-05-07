import {Component, ElementRef, ViewChild} from '@angular/core';
import {
  CreateHelper,
  DrawableEntity,
  DrawHelper,
  Helper,
  InteractableEntity,
  InteractionHelper,
  MoveHelper
} from "./helpers";
import {NgIf} from "@angular/common";
import {BackgroundEntity, SeatEntity, SectionEntity} from "./entities";

@Component({
  selector: 'app-hallplan',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './hallplan.component.html',
  styleUrl: './hallplan.component.scss'
})
export class HallplanComponent {
  @ViewChild('hallplanCanvas') myCanvas: ElementRef<HTMLCanvasElement>;
  ctx: CanvasRenderingContext2D;
  width = 1000;
  height = 1000;
  widthInMeters = 800;
  heightInMeters = 600;
  backgroundImage: HTMLImageElement;
  backgroundImageUrl: string;

  sections: HallSection[] = [
    // generate 5 random sections

    // Example: Section 1
    {
      points: [
        { x: 100, y: 150 },
        { x: 200, y: 100 },
        { x: 200, y: 200 },
        { x: 100, y: 200 }
      ],
      name: 'Section 1',
      color: 'red',
      price: 100,
      seats: []
    },
   ];

  drawHelper: DrawHelper;
  moveHelper: MoveHelper;
  createHelper: CreateHelper;
  interactionHelper: InteractionHelper;
  additionalHelpers: Helper[] = [];
  additionalHelpersReversed: Helper[] = [];

  seats: HallSeat[] = [];

  entities: DrawableEntity[] = [];

  async ngOnInit() {
    this.backgroundImageUrl = `https://placehold.co/${this.width}x${this.height}`;
  }

  loadImage(url: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const image = new Image();
      image.onload = () => resolve(image);
      image.onerror = (error) => reject(error);
      image.src = url;
    });
  }

  async ngAfterViewInit() {
    // After the view has been initialized, you can access the canvas element here
    this.backgroundImage = await this.loadImage(this.backgroundImageUrl);
    const canvas: HTMLCanvasElement = this.myCanvas.nativeElement;
    this.ctx = canvas.getContext('2d');

    this.generateEntites();

    this.additionalHelpers.push(this.drawHelper = new DrawHelper());
    this.additionalHelpers.push(this.moveHelper = new MoveHelper(this.drawHelper, canvas, this.width, this.height));
    this.additionalHelpers.push(this.createHelper = new CreateHelper(this.drawHelper, canvas, this.sections, this.seats, this.ctx));
    const interactableEntities = this.entities.filter(entity => 'getActions' in entity).map(entity => entity as unknown as InteractableEntity);
    this.additionalHelpers.push(this.interactionHelper = new InteractionHelper(this.drawHelper, canvas, interactableEntities));

    this.additionalHelpersReversed = this.additionalHelpers.slice().reverse();

    this.refreshCanvas(true);
    this.setupCanvasRefresh();
  }

  generateEntites() {
    this.entities.splice(0, this.entities.length);
    this.entities.push(new BackgroundEntity(this.backgroundImage, this.width, this.height));
    this.entities.push(...this.sections.map(section => new SectionEntity(section)));
    this.entities.push(...this.sections.map(section => section.seats.map(seat => new SeatEntity(seat))).reduce((acc, val) => acc.concat(val), []));
    const interactableEntities = this.entities.filter(entity => 'getActions' in entity).map(entity => entity as unknown as InteractableEntity);
    this.interactionHelper?.setEntities(interactableEntities);
  }

  finishSection() {
    const sectionPolygon = this.createHelper.getSectionPolygon();
    if (sectionPolygon.length > 2) {
      this.sections.push({
        points: sectionPolygon,
        name: 'Section ' + (this.sections.length + 1),
        color: 'blue',
        price: 100,

        seats: this.createHelper.generateSeats().map(pos => ({ pos })),
      });
      this.createHelper.disable();
      this.generateEntites();

      this.refreshCanvas(true);
    }
  }

  setupCanvasRefresh() {
    this.refreshCanvas();
    requestAnimationFrame(() => this.setupCanvasRefresh());
  }

  refreshCanvas(forceRedraw = false) {
    if (forceRedraw || this.additionalHelpers.filter(helper => helper.enabled).map(helper => helper.frameHasChanged).some(val => val === true)) {
      this.additionalHelpers.filter(helper => helper.enabled).forEach(helper => helper.onDrawCanvas(this.ctx, this.entities));
      this.additionalHelpers.filter(helper => helper.enabled).forEach(helper => helper.resetFrameHasChanged());
    }
  }

  zoomIn() {
    this.moveHelper.zoomAtPoint(1.1, { x: this.width / 2, y: this.height / 2 });
  }
  zoomOut() {
    this.moveHelper.zoomAtPoint(0.9, { x: this.width / 2, y: this.height / 2 });
  }

  onMouseScroll(event: WheelEvent) {
    event.preventDefault();
    this.additionalHelpersReversed.filter(helper => helper.enabled).some(helper => helper.onMouseScroll(event));
  }

  onMouseMove(event: MouseEvent) {
    event.preventDefault();
    this.additionalHelpersReversed.filter(helper => helper.enabled).some(helper => helper.onMouseMove(event));
  }

  onMouseUp(event: MouseEvent) {
    event.preventDefault();
    this.additionalHelpersReversed.filter(helper => helper.enabled).some(helper => helper.onMouseUp(event));
  }

  onMouseDown(event: MouseEvent) {
    event.preventDefault();
    this.additionalHelpersReversed.filter(helper => helper.enabled).some(helper => helper.onMouseDown(event));
  }
}



export type HallSection = {
  points: Point2D[];
  name: string;
  color: string;
  price: number;

  seats: HallSeat[];
}

export type HallSeat = {
  pos: Point2D;
}

export type Point2D = {
  x: number;
  y: number;
}
