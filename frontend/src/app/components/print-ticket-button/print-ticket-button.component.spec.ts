import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintTicketButtonComponent } from './print-ticket-button.component';

describe('PrintTicketButtonComponent', () => {
  let component: PrintTicketButtonComponent;
  let fixture: ComponentFixture<PrintTicketButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrintTicketButtonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrintTicketButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
