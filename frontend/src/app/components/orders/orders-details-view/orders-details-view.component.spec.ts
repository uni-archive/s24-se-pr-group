import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdersDetailsViewComponent } from './orders-details-view.component';

describe('OrdersDetailsViewComponent', () => {
  let component: OrdersDetailsViewComponent;
  let fixture: ComponentFixture<OrdersDetailsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrdersDetailsViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrdersDetailsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
