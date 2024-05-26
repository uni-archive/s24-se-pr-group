import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutocompleteTextfieldComponent } from './autocomplete-textfield.component';

describe('AutocompleteTextfieldComponent', () => {
  let component: AutocompleteTextfieldComponent;
  let fixture: ComponentFixture<AutocompleteTextfieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutocompleteTextfieldComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AutocompleteTextfieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
