import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditCardExpiryInputComponent } from './credit-card-expiry-input.component';

describe('CreditCardExpiryInputComponent', () => {
  let component: CreditCardExpiryInputComponent;
  let fixture: ComponentFixture<CreditCardExpiryInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreditCardExpiryInputComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreditCardExpiryInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
