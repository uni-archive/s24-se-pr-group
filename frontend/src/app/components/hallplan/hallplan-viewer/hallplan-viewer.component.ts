import { Component, Input, OnChanges, SimpleChanges, ViewChild, AfterViewInit } from '@angular/core';
import { HallPlanDto } from '../../../services/openapi';
import { HallplanComponent } from '../hallplan.component';

@Component({
  selector: 'app-hallplan-viewer',
  templateUrl: './hallplan-viewer.component.html',
  styleUrls: ['./hallplan-viewer.component.scss']
})
export class HallPlanViewerComponent implements OnChanges, AfterViewInit {
  @Input() hallPlan: HallPlanDto | null = null;
  @ViewChild('hallplan') hallplan: HallplanComponent;

  private viewInitialized = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.hallPlan && this.hallPlan) {
      if (this.viewInitialized) {
        this.updateHallPlan(this.hallPlan);
      }
    }
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;
    if (this.hallPlan) {
      this.updateHallPlan(this.hallPlan);
    }
  }

  private async updateHallPlan(hallPlan: HallPlanDto): Promise<void> {
    if (this.hallplan) {
      this.hallplan.sections = hallPlan.sectors.map(section => {
        return {
          id: section.id,
          name: section.name,
          color: section.color,
          isStandingOnly: false,
          availableSpotCount: 0,
          price: 0,
          points: JSON.parse(section.frontendCoordinates),
          seats: section.seats.map(seat => {
            return {
              id: seat.id,
              sectorId: section.id,
              isAvailable: true,
              pos: JSON.parse(seat['frontendCoordinates']),
            };
          })
        };
      });
      this.hallplan.generateEntities();
      this.hallplan.refreshCanvas(true);
      await this.hallplan.setBackgroundImage(hallPlan.backgroundImage);
    }
  }
}
