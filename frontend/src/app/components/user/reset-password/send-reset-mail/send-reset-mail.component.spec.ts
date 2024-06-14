import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendResetMailComponent } from './send-reset-mail.component';

describe('SendResetMailComponent', () => {
  let component: SendResetMailComponent;
  let fixture: ComponentFixture<SendResetMailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendResetMailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SendResetMailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
