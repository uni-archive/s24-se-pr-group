import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintCancellationInvoiceButtonComponent } from './print-cancellation-invoice-button.component';

describe('PrintCancellationInvoiceButtonComponent', () => {
  let component: PrintCancellationInvoiceButtonComponent;
  let fixture: ComponentFixture<PrintCancellationInvoiceButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrintCancellationInvoiceButtonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrintCancellationInvoiceButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
