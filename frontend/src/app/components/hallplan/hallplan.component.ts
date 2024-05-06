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
  posX = 0;
  posY = 0;
  mouseX: number;
  mouseY: number;
  backgroundImage: HTMLImageElement;
  backgroundImageUrl: string;
  isDragging = false;

  dragStartX: number;
  dragStartY: number;

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
    this.drawCanvas();
  }

  onMouseScroll(event: WheelEvent) {
    // Handle mouse scroll event here

    // Example: Zooming in/out based on deltaY value
    const zoomFactor = event.deltaY > 0 ? 1.1 : 0.9; // Adjust as needed
    this.localScale = this.clamp(zoomFactor * this.localScale, 1, 10);
    this.posX -= (this.mouseX - this.posX) * (zoomFactor - 1);
    this.posY -= (this.mouseY - this.posY) * (zoomFactor - 1);
    this.posX = this.clamp(this.posX, 0, this.width * this.localScale - this.width);
    this.posY = this.clamp(this.posY, 0, this.height * this.localScale - this.height);
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
    this.ctx.clearRect(0, 0, this.width, this.height);
    this.drawBackgroundImage();
  }

  drawBackgroundImage() {
    console.log(this.posX, this.posY, this.localScale, this.isDragging)
    this.ctx.drawImage(this.backgroundImage, this.posX, this.posY, this.width * this.localScale, this.height * this.localScale);
  }
}
