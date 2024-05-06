import {Component, ElementRef, ViewChild} from '@angular/core';

@Component({
  selector: 'app-hallplan',
  standalone: true,
  imports: [],
  templateUrl: './hallplan.component.html',
  styleUrl: './hallplan.component.scss'
})
export class HallplanComponent {
  @ViewChild('hallplanCanvas') myCanvas: ElementRef<HTMLCanvasElement>;
  ctx: CanvasRenderingContext2D;
  localScale = 1;
  width = 800;
  height = 600;
  widthInMeters = 800;
  heightInMeters = 600;
  posX = 0;
  posY = 0;
  mouseX: number;
  mouseY: number;
  backgroundImage: HTMLImageElement;
  backgroundImageUrl: string;
  isDragging = false;

  dragStartX: number;
  dragStartY: number;

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
      price: 100
    },
   ];

  circleRadius = 10; // Radius of the circles
  circleMargin = 5; // Margin between circles

  seats: HallSeat[] = [];



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
    const canvas: HTMLCanvasElement = this.myCanvas.nativeElement;
    const ctx = this.ctx = canvas.getContext('2d');
    this.backgroundImage = await this.loadImage(this.backgroundImageUrl);
    console.log(this.seats)
    this.generateSeats(this.sections[0]);
    this.drawCanvas();
  }

  onMouseScroll(event: WheelEvent) {
    // Handle mouse scroll event here

    // Example: Zooming in/out based on deltaY value
    const zoomFactor = event.deltaY > 0 ? 1.1 : 0.9; // Adjust as needed
    const prevScale = this.localScale;
    this.localScale = this.clamp(zoomFactor * this.localScale, 1, 10);
    if (prevScale !== this.localScale) {
      this.posX -= (this.mouseX - this.posX) * (zoomFactor - 1);
      this.posY -= (this.mouseY - this.posY) * (zoomFactor - 1);
    }
    // this.posX = this.clamp(this.posX, 0, this.width * this.localScale - this.width);
    // this.posY = this.clamp(this.posY, 0, this.height * this.localScale - this.height);
    this.drawCanvas();
  }

  onMouseMove(event: MouseEvent) {
    const rect = this.myCanvas.nativeElement.getBoundingClientRect();
    this.mouseX = event.clientX - rect.left;
    this.mouseY = event.clientY - rect.top;

    if (this.isDragging) {
      // Calculate the distance moved
      const deltaX = event.clientX - this.dragStartX;
      const deltaY = event.clientY - this.dragStartY;

      // Update the canvas position
      this.posX += deltaX;
      this.posY += deltaY;

      // Update the drag start position
      this.dragStartX = event.clientX;
      this.dragStartY = event.clientY;

      // Redraw the canvas
      this.drawCanvas();
    }
  }

  onMouseUp(event: MouseEvent) {
    // Stop dragging
    this.isDragging = false;
  }

  onMouseDown(event: MouseEvent) {
    // Start dragging
    this.isDragging = true;
    this.dragStartX = event.clientX;
    this.dragStartY = event.clientY;
  }

  clamp(value: number, min: number, max: number) {
    return Math.min(Math.max(value, min), max);
  }

  drawCanvas() {
    console.log(this.backgroundImage)
    this.drawBackgroundImage();
    this.drawAllSections();
    this.drawAllSeats();
  }

  drawBackgroundImage() {
    console.log(this.posX, this.posY, this.localScale, this.isDragging)
    this.ctx.drawImage(this.backgroundImage, this.posX, this.posY, this.width * this.localScale, this.height * this.localScale);
  }

  drawAllSections() {
    this.sections.forEach(section => this.drawSection(section));
  }

  drawSection(section: HallSection) {
    this.ctx.fillStyle = 'red';
    this.ctx.beginPath();
    section.points.forEach((point, index) => {
      // move to points with respect to scale and pos
      const x = point.x * this.localScale + this.posX;
      const y = point.y * this.localScale + this.posY;
      if (index === 0) {
        this.ctx.moveTo(x, y);
      } else {
        this.ctx.lineTo(x, y);
      }
    });
    this.ctx.closePath();
    this.ctx.fill();
  }

  drawAllSeats() {
    var i = 0;
    this.seats.forEach(seat => {
      this.drawCircle(seat.pos.x, seat.pos.y);
      console.log(++i)
    });
  }

  drawCircle(x: number, y: number) {
    // idk why but arc messes everything up
    // this.ctx.fillStyle = 'rgba(0, 0, 255, 0.4)';
    // this.ctx.arc(x * this.localScale + this.posX, y * this.localScale + this.posY, this.circleRadius * this.localScale / 2, 0, 2 * Math.PI);
    // this.ctx.fill();
    // this.ctx.closePath();
    this.ctx.fillStyle = 'rgba(0, 0, 255, 0.4)';
    this.ctx.fillRect(x * this.localScale + this.posX, y * this.localScale + this.posY, this.circleRadius * this.localScale, this.circleRadius * this.localScale);
  }

  generateSeats(section: HallSection) {
    const { max, min} = this.getMinMaxPoints(section.points);
    const padding = 5;

    const sectionPath = new Path2D();
    section.points.forEach((point, index) => {
      // move to points with respect to scale and pos
      const x = point.x;
      const y = point.y;
      if (index === 0) {
        sectionPath.moveTo(x, y);
      } else {
        sectionPath.lineTo(x, y);
      }
    });

    for (let x = min.x + this.circleRadius / 2 + padding; x < max.x - padding - this.circleRadius / 2; x += this.circleRadius + padding) {
      for (let y = min.y + this.circleRadius / 2 + padding; y < max.y - padding - this.circleRadius / 2; y += this.circleRadius + padding) {
        console.log('Seat is inside section?', this.ctx.isPointInPath(sectionPath, x, y), x, y, 'section:', section.name, 'color:', section.color, 'price:', section.price);
        if (this.ctx.isPointInPath(sectionPath, x, y)) {
          this.seats.push({ pos: { x, y } });
        }
      }
    }
    sectionPath.closePath()
  }

  getMinMaxPoints(points: Point2D[]): { min: Point2D, max: Point2D } {
    const xs = points.map(p => p.x);
    const ys = points.map(p => p.y);
    return {
      min: { x: Math.min(...xs), y: Math.min(...ys) },
      max: { x: Math.max(...xs), y: Math.max(...ys) }
    };
  }
}



type HallSection = {
  points: Point2D[];
  name: string;
  color: string;
  price: number;
}

type HallSeat = {
  pos: Point2D;
}

type Point2D = {
  x: number;
  y: number;
}
