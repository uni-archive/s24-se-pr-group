import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {
  @Output() confirm = new EventEmitter<boolean>();
  @Input() customerName: string;
  @Input() action: string = 'mark as picked up'; // Default action text

  onNoClick(): void {
    this.confirm.emit(false);
  }

  onYesClick(): void {
    this.confirm.emit(true);
  }
}
