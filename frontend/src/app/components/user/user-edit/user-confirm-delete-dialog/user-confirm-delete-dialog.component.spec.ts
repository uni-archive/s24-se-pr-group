import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserConfirmDeleteDialogComponent } from './user-confirm-delete-dialog.component';

describe('UserConfirmDeleteDialogComponent', () => {
  let component: UserConfirmDeleteDialogComponent;
  let fixture: ComponentFixture<UserConfirmDeleteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserConfirmDeleteDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserConfirmDeleteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
