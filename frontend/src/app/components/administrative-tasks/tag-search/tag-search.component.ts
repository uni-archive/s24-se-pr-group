import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf, NgTemplateOutlet} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ArtistDto} from "../../../services/openapi";

@Component({
  selector: "app-tag-search",
  standalone: true,
  imports: [NgForOf, ReactiveFormsModule, FormsModule,
    NgTemplateOutlet
  ],
  templateUrl: "./tag-search.component.html",
  styleUrl: "./tag-search.component.scss",
})
export class TagSearchComponent {
  @Input() autoCompleteList: Iterable<ArtistDto>;
  @Input() placeholder: string;
  @Input() Label: string;
  @Input() chosenArtists: ArtistDto[];
  @Output() search: EventEmitter<string> = new EventEmitter<string>();
  @Output() itemChosen: EventEmitter<ArtistDto> = new EventEmitter<ArtistDto>();
  @Output() artistRemoved: EventEmitter<ArtistDto> = new EventEmitter<ArtistDto>();
  public text: string = "";
  onChange() {
    this.search.emit(this.text);
  }

  removeArtist(delart: ArtistDto) {
    this.artistRemoved.emit(delart);
  }

  eventClicked(event: ArtistDto) {
    this.itemChosen.emit(event);
    this.text = "";
    this.autoCompleteList = [];
  }
}
