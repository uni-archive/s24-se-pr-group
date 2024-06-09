import { ChangeDetectorRef, Component } from '@angular/core';
import { NewsEndpointService, NewsResponseDto } from "../../../services/openapi";
import { NgbPaginationConfig } from "@ng-bootstrap/ng-bootstrap";
import { UntypedFormBuilder } from "@angular/forms";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent {
  error = false;
  errorMessage = '';
  success = false;
  successMessage = '';

  submitted = false;

  currentNews: NewsResponseDto = {} as NewsResponseDto;
  news: NewsResponseDto[] = [];
  allNews: NewsResponseDto[] = [];
  unreadNews: NewsResponseDto[] = [];
  newsMode: 'ALL' | 'UNREAD' = 'ALL';
  selectedFile: File;

  totalElements: number = 0;
  pageSize: number = 9;
  currentPage: number = 1;

  constructor(
      private ngbPaginationConfig: NgbPaginationConfig,
      private formBuilder: UntypedFormBuilder,
      private cd: ChangeDetectorRef,
      private authService: AuthService,
      private newsService: NewsEndpointService
  ) {}

  ngOnInit() {
    this.currentNews = {} as NewsResponseDto;
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  onFileSelected(event): void {
    this.selectedFile = event.target.files[0];
  }

  selectImage(): void {
    const fileInput = document.getElementById('inputImage');
    fileInput.click();
  }

  addNews(form): void {
    this.submitted = true;

    if (form.valid && this.selectedFile) {
      this.createNews(this.currentNews);
      this.clearForm();
    }
  }

  private clearForm(): void {
    this.currentNews = {} as NewsResponseDto;
    this.submitted = false;
  }

  vanishError(): void {
    this.error = false;
  }

  vanishSuccess(): void {
    this.success = false;
  }

  private createNews(news: NewsResponseDto): void {
    this.newsService.create(news.title, news.summary, news.text, this.selectedFile).subscribe({
      next: () => {
        this.successMessage = 'Die News wurde erfolgreich gespeichert.';
        this.success = true;
        setTimeout(() => {
          this.success = false;
        }, 5000);
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
        this.success = false;
      }
    });
  }

  private defaultServiceErrorHandling(error: any): void {
    console.log(error);
    this.error = true;
    if (error.error && error.error.detail) {
      this.errorMessage = error.error.detail;
    } else if (error.error && error.error.message) {
      this.errorMessage = error.error.message;
    } else if (error.error && typeof error.error === 'object') {
      this.errorMessage = JSON.stringify(error.error);
    } else if (error.message) {
      this.errorMessage = error.message;
    } else {
      this.errorMessage = 'Ein unbekannter Fehler ist aufgetreten.';
    }
  }
}
