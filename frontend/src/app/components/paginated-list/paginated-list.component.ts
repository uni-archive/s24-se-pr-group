import { Component, EventEmitter, Input, OnInit, Output, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-paginated-list',
  templateUrl: './paginated-list.component.html',
  styleUrls: ['./paginated-list.component.scss']
})
export class PaginatedListComponent implements OnInit {
  @Input() filterTemplate!: TemplateRef<any>;
  @Input() resultTemplate!: TemplateRef<any>;
  @Input() searchFunction!: (criteria: any, page: number, size: number) => any;
  @Input() showPaginationButtons: boolean = true;
  @Input() filterConfig!: any; // New input to accept filter configuration

  @Output() createNew = new EventEmitter<void>();

  searchForm: FormGroup = new FormGroup({}); // Initialize an empty form group
  items: any[] = [];
  error: string | null = null;
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // Dynamically create form controls based on filterConfig
    this.filterConfig.forEach((controlName: string) => {
      this.searchForm.addControl(controlName, new FormControl(''));
    });

    this.searchForm.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.currentPage = 1;
      this.searchItems();
    });

    this.searchItems();
  }

  searchItems(): void {
    this.error = null;
    const criteria = this.searchForm.value;
    this.searchFunction(criteria, this.currentPage - 1, this.pageSize).subscribe({
      next: (data: any) => {
        this.items = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => {
        this.error = err.message;
      }
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
