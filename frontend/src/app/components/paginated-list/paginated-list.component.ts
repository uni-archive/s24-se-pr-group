import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  SimpleChanges,
  OnChanges,
} from "@angular/core";
import { FormBuilder, FormGroup, FormControl } from "@angular/forms";
import { debounceTime } from "rxjs/operators";

@Component({
  selector: "app-paginated-list",
  templateUrl: "./paginated-list.component.html",
  styleUrls: ["./paginated-list.component.scss"],
})
export class PaginatedListComponent implements OnInit, OnChanges {
  @Input() filterTemplate!: TemplateRef<any>;
  @Input() resultTemplate!: TemplateRef<any>;
  @Input() searchFunction!: (criteria: any, page: number, size: number) => any;
  @Input() showPaginationButtons: boolean = true;
  @Input() filterConfig: { [key: string]: any } = {}; // Accept filter configuration with default values
  @Input() createButtonEnabled: boolean = false;
  @Input() heading: string = "";
  @Input() refresh: boolean = false; // Input to trigger refresh

  @Output() createNew = new EventEmitter<void>();

  searchForm: FormGroup = new FormGroup({}); // Initialize an empty form group
  items: any[] = [];
  error: string | null = null;
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initializeFormControls();
    this.searchForm.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.currentPage = 1;
      this.searchItems();
    });

    this.searchItems();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["refresh"] && !changes["refresh"].firstChange) {
      this.searchItems();
    }
    if (changes["filterConfig"]) {
      this.initializeFormControls();
    }
  }

  private initializeFormControls(): void {
    // Clear the form controls if they exist
    Object.keys(this.searchForm.controls).forEach((controlName) => {
      this.searchForm.removeControl(controlName);
    });

    // Dynamically create form controls based on filterConfig
    Object.keys(this.filterConfig).forEach((controlName: string) => {
      this.searchForm.addControl(
        controlName,
        new FormControl(this.filterConfig[controlName] ?? "")
      );
    });
  }

  searchItems(): void {
    this.error = null;
    const criteria = this.searchForm.value;
    this.searchFunction(
      criteria,
      this.currentPage - 1,
      this.pageSize
    ).subscribe({
      next: (data: any) => {
        this.items = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => {
        this.error = err.message;
      },
    });
  }

  changePage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.searchItems();
    }
  }

  getPages(): number[] {
    const pages = [];
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }
}
