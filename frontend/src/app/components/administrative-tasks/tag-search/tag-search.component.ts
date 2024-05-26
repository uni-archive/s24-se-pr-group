import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Artist, EventDto} from "../../../services/openapi";

@Component({
  selector: 'app-tag-search',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './tag-search.component.html',
  styleUrl: './tag-search.component.scss'
})
export class TagSearchComponent {

  @Input() autoCompleteList :Iterable<Artist>;
  @Input() placeholder: string;
  @Input() Label: string;
  @Input() chosenArtists : Artist[];
  @Output() search: EventEmitter<string> = new EventEmitter<string>();
  @Output() itemChosen : EventEmitter<Artist> = new EventEmitter<Artist>();
  @Output() artistRemoved: EventEmitter<Artist> = new EventEmitter<Artist>();
  public text: string = "";
  onChange() {
    console.log(this.autoCompleteList);
    this.search.emit(this.text);
  }

  removeArtist(delart:Artist) {
    this.artistRemoved.emit(delart);
  }

  eventClicked(event : Artist) {
    this.itemChosen.emit(event);
    this.text = "";
    this.autoCompleteList = [];
  }
}
