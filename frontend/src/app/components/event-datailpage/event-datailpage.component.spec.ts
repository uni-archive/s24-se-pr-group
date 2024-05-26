import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventDatailpageComponent } from './event-datailpage.component';

describe('EventDatailpageComponent', () => {
  let component: EventDatailpageComponent;
  let fixture: ComponentFixture<EventDatailpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventDatailpageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EventDatailpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
