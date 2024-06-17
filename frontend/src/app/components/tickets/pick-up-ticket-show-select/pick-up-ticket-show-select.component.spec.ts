import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickUpTicketShowSelectComponent } from './pick-up-ticket-show-select.component';

describe('PickUpTicketShowSelectComponent', () => {
  let component: PickUpTicketShowSelectComponent;
  let fixture: ComponentFixture<PickUpTicketShowSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickUpTicketShowSelectComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PickUpTicketShowSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
