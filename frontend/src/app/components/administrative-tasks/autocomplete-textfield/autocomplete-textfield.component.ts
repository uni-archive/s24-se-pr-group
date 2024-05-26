import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Artist, EventDto, ShowDto} from "../../../services/openapi";

@Component({
  selector: 'app-autocomplete-textfield',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule
  ],
  templateUrl: './autocomplete-textfield.component.html',
  styleUrl: './autocomplete-textfield.component.scss'
})
export class AutocompleteTextfieldComponent {

  @Input() autoCompleteList :Iterable<any>;
  @Input() placeholder: string;
  @Input() Label: string;
  @Output() search: EventEmitter<string> = new EventEmitter<string>();
  @Output() eventChosen : EventEmitter<EventDto|Artist> = new EventEmitter<EventDto|Artist>();
  public text: string = "";
  onChange() {
    console.log(this.autoCompleteList);
    this.search.emit(this.text);
  }

  eventClicked(event : EventDto) {
    this.eventChosen.emit(event);
    this.text = event.title;
    this.autoCompleteList = [];
  }

}
