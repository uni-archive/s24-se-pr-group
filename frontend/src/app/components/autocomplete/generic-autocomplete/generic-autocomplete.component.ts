import {Component, EventEmitter, forwardRef, Input, OnInit, Output, TemplateRef} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-generic-autocomplete',
  templateUrl: './generic-autocomplete.component.html',
  styleUrls: ['./generic-autocomplete.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => GenericAutocompleteComponent),
    multi: true
  }]
})
export class GenericAutocompleteComponent<T> implements OnInit, ControlValueAccessor {
  @Input() searchFunction!: (query: string) => Observable<T[]>;
  @Input() displayTemplate!: TemplateRef<any>;
  @Input() placeholder: string = 'Search and select an item';
  @Input() label: string = 'Search';
  @Input() textExtraction: (item: T) => string = null;
  @Input() selectedItem: T | null = null;

  @Output() itemSelected = new EventEmitter<T>();
  @Output() itemReset = new EventEmitter<void>();

  searchForm: FormGroup;
  filteredItems$: Observable<T[]>;
  showSuggestions: boolean = false;

  private onChange: any = () => {
  };
  private onTouched: any = () => {
  };

  constructor(private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      search: ['']
    });
  }

  ngOnInit(): void {
    this.filteredItems$ = this.searchForm.get('search')!.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => value.trim() ? this.searchFunction(value) : of([]))
    );
    console.log("selectedItem::")
    console.log(this.selectedItem)
    if (this.selectedItem) {
      this.searchForm.get('search')!.setValue(this.getItemDisplayName(this.selectedItem), {emitEvent: false});
    }
  }

  ngOnChanges(): void {
    if (this.selectedItem) {
      this.searchForm.get('search')!.setValue(this.getItemDisplayName(this.selectedItem), {emitEvent: false});
    }
  }

  selectItem(item: T): void {
    this.itemSelected.emit(item);
    this.searchForm.get('search')!.setValue(this.getItemDisplayName(item), {emitEvent: false});
    this.showSuggestions = false;
    this.onChange(item);
    this.onTouched();
  }

  resetSelection(): void {
    this.searchForm.get('search')!.setValue('', {emitEvent: false});
    this.itemReset.emit();
    this.showSuggestions = false;
    this.onChange(null);
    this.onTouched();
  }

  onInputFocus(): void {
    this.showSuggestions = true;
    const searchValue = this.searchForm.get('search')!.value.trim();
    if (searchValue) {
      this.filteredItems$ = this.searchFunction(searchValue);
    }
  }

  onInputBlur(): void {
    setTimeout(() => this.showSuggestions = false, 200);
  }

  getItemDisplayName(item: T): string {
    if (this.textExtraction !== null) {
      return this.textExtraction(item);
    }
    return (item as any).name;
  }

  // ControlValueAccessor interface methods
  writeValue(obj: T): void {
    if (obj) {
      this.selectedItem = obj;
      this.searchForm.get('search')!.setValue(this.getItemDisplayName(obj), {emitEvent: false});
    } else {
      this.selectedItem = null;
      this.searchForm.get('search')!.setValue('', {emitEvent: false});
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    if (isDisabled) {
      this.searchForm.disable();
    } else {
      this.searchForm.enable();
    }
  }
}
