import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NewsEndpointService, NewsResponseDto } from "../../../services/openapi";
import { NgbPaginationConfig } from "@ng-bootstrap/ng-bootstrap";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {
  error = false;
  errorMessage = '';
  success = false;
  successMessage = '';

  submitted = false;
  newsForm: FormGroup;
  selectedFile: File | null = null;

  constructor(
      private ngbPaginationConfig: NgbPaginationConfig,
      private formBuilder: FormBuilder,
      private cd: ChangeDetectorRef,
      private authService: AuthService,
      private newsService: NewsEndpointService
  ) {}

  ngOnInit() {
    this.newsForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      summary: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]],
      text: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(10000)]],
      image: [null, Validators.required]
    });
  }

  get formControls() {
    return this.newsForm.controls;
  }

  onFileSelected(event): void {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.selectedFile = file;
      this.newsForm.patchValue({ image: file });
    } else {
      this.newsForm.patchValue({ image: null });
      this.selectedFile = null;
      this.errorMessage = 'Nur Bilddateien sind erlaubt.';
      this.error = true;
    }
  }

  addNews(): void {
    this.submitted = true;

    if (this.newsForm.invalid || !this.selectedFile) {
      return;
    }

    this.createNews(this.newsForm.value);
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

  vanishError(): void {
    this.error = false;
  }

  vanishSuccess(): void {
    this.success = false;
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
