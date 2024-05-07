import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-interactive-canvas',
  templateUrl: './interactive-canvas.component.html',
  styleUrls: ['./interactive-canvas.component.scss']
})
export class InteractiveCanvasComponent implements AfterViewInit {
  @ViewChild('canvas', { static: true })
  canvas: ElementRef<HTMLCanvasElement>;

  private ctx: CanvasRenderingContext2D;
  private rectangles = [
    { id: 1, x: 50, y: 50, width: 100, height: 100, rotation: 0 },
    // Add more rectangles as needed
    { id: 2, x: 500, y: 500, width: 100, height: 100, rotation: 0 },

  ];

  ngAfterViewInit(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');
    this.draw();
  }

  draw(): void {
    this.rectangles.forEach(rect => {
      this.ctx.save();
      this.ctx.translate(rect.x + rect.width / 2, rect.y + rect.height / 2);
      this.ctx.rotate(rect.rotation * Math.PI / 180);
      this.ctx.fillStyle = 'rgba(0, 0, 255, 0.5)';
      this.ctx.fillRect(-rect.width / 2, -rect.height / 2, rect.width, rect.height);
      this.ctx.restore();
    });
  }

  onCanvasClick(event: MouseEvent): void {
    const rect = this.canvas.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const clickedRect = this.rectangles.find(r =>
      x > r.x && x < r.x + r.width && y > r.y && y < r.y + r.height);

    if (clickedRect) {
      // Send the ID to the backend
      console.log(`Rectangle ${clickedRect.id} was clicked.`);
      // Implement the logic to send this ID to your backend here
    }
  }
}
