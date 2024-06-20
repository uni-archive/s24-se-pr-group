import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallplanViewerComponent } from './hallplan-viewer.component';

describe('HallplanViewerComponent', () => {
  let component: HallplanViewerComponent;
  let fixture: ComponentFixture<HallplanViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HallplanViewerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HallplanViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
