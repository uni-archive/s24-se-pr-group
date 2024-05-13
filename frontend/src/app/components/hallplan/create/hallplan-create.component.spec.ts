import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallplanCreateComponent } from './hallplan-create.component';

describe('CreateComponent', () => {
  let component: HallplanCreateComponent;
  let fixture: ComponentFixture<HallplanCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HallplanCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallplanCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
