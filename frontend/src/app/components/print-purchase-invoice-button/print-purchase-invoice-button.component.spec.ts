import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintPurchaseInvoiceButtonComponent } from './print-purchase-invoice-button.component';

describe('PrintReceiptButtonComponent', () => {
  let component: PrintPurchaseInvoiceButtonComponent;
  let fixture: ComponentFixture<PrintPurchaseInvoiceButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrintPurchaseInvoiceButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrintPurchaseInvoiceButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
