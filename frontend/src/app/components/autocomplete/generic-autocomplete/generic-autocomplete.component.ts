import { Component, OnInit, Output, EventEmitter, Input, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';
import {EventDto} from "../../../services/openapi";

@Component({
  selector: 'app-generic-autocomplete',
  templateUrl: './generic-autocomplete.component.html',
  styleUrls: ['./generic-autocomplete.component.scss']
})
export class GenericAutocompleteComponent<T> implements OnInit {
  @Input() searchFunction!: (query: string) => Observable<T[]>;
  @Input() displayTemplate!: TemplateRef<any>;
  @Input() placeholder: string = 'Search and select an item';
  @Input() label: string = 'Search';
  @Input() textExtraction : (item : T) => string = null;

  @Output() itemSelected = new EventEmitter<T>();

  searchForm: FormGroup;
  filteredItems$: Observable<T[]>;
  showSuggestions: boolean = false;

  constructor(private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      search: ['']
    });
  }

  ngOnInit(): void {
    this.filteredItems$ = this.searchForm.get('search')!.valueChanges.pipe(
      debounceTime(300),
      switchMap(value => this.searchFunction(value))
    );
  }

  selectItem(item: T): void {
    this.itemSelected.emit(item);
    this.searchForm.get('search')!.setValue(this.getItemDisplayName(item), { emitEvent: false });
    this.showSuggestions = false;
  }

  onInputFocus(): void {
    this.showSuggestions = true;
    this.filteredItems$ = this.searchFunction(this.searchForm.get('search')!.value);
  }

  onInputBlur(): void {
    setTimeout(() => this.showSuggestions = false, 200);
  }

  getItemDisplayName(item: T): string {

    if (this.textExtraction !== null) {
      return this.textExtraction(item);
    }
    // Customize this method to extract the display name from the item
    return (item as any).name;
  }
}
