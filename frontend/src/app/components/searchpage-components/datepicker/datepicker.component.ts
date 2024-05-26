import {Component, EventEmitter, Input, Output} from '@angular/core';
import { NgbAlertModule, NgbDatepickerModule, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-datepicker',
  standalone: true,
  imports: [NgbDatepickerModule, NgbAlertModule, FormsModule, JsonPipe],
  templateUrl: './datepicker.component.html',
  styleUrl: './datepicker.component.scss'
})
export class DatepickerComponent {

  @Output() datechange = new EventEmitter<Date>();
  @Input() label: string;

  model: NgbDateStruct;

  dateChange() {
    try {
      this.datechange.emit(new Date(this.model.year,this.model.month-1, this.model.day));

    }catch (ex) {
      this.datechange.emit(null);
    }
  }
}
