import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallplanComponent } from './hallplan.component';

describe('HallplanComponent', () => {
  let component: HallplanComponent;
  let fixture: ComponentFixture<HallplanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HallplanComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HallplanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
