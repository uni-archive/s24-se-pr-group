import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmCancelTicketReservationDialogComponent } from './confirm-cancel-ticket-reservation-dialog.component';

describe('ConfirmCancelTicketReservationDialogComponent', () => {
  let component: ConfirmCancelTicketReservationDialogComponent;
  let fixture: ComponentFixture<ConfirmCancelTicketReservationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmCancelTicketReservationDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConfirmCancelTicketReservationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
