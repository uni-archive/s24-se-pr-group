import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmCancelOrderDialogComponent } from './confirm-cancel-order-dialog.component';

describe('ConfirmCancelOrderDialogComponent', () => {
  let component: ConfirmCancelOrderDialogComponent;
  let fixture: ComponentFixture<ConfirmCancelOrderDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmCancelOrderDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConfirmCancelOrderDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
