import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagSearchComponent } from './tag-search.component';

describe('TagSearchComponent', () => {
  let component: TagSearchComponent;
  let fixture: ComponentFixture<TagSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagSearchComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TagSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
