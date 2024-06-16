import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickUpTicketsComponent } from './pick-up-tickets.component';

describe('PickUpTicketsComponent', () => {
  let component: PickUpTicketsComponent;
  let fixture: ComponentFixture<PickUpTicketsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickUpTicketsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PickUpTicketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
