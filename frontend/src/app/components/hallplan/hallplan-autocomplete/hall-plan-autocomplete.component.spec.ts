import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallPlanAutocompleteComponent } from './hall-plan-autocomplete.component';

describe('HallplanAutocompleteComponent', () => {
  let component: HallPlanAutocompleteComponent;
  let fixture: ComponentFixture<HallPlanAutocompleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HallPlanAutocompleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallPlanAutocompleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
