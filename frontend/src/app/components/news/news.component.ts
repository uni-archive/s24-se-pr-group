import { ChangeDetectorRef, Component, OnInit, TemplateRef } from '@angular/core';
import { NgbModal, NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { UntypedFormBuilder } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { NewsResponseDto, NewsEndpointService } from "../../services/openapi";

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {
  error = false;
  errorMessage = '';
  success = false;
  successMessage = '';

  submitted = false;

  currentNews: NewsResponseDto;
  news: NewsResponseDto[] = [];
  allNews: NewsResponseDto[] = [];
  unreadNews: NewsResponseDto[] = [];
  newsMode: 'ALL' | 'UNREAD' = 'ALL';
  selectedFile: File;

  constructor(
      private ngbPaginationConfig: NgbPaginationConfig,
      private formBuilder: UntypedFormBuilder,
      private cd: ChangeDetectorRef,
      private authService: AuthService,
      private modalService: NgbModal,
      private newsService: NewsEndpointService
  ) {}

  ngOnInit() {
    this.newsMode = 'UNREAD';
    this.loadNews();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
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

  openAddModal(newsAddModal: TemplateRef<any>) {
    this.currentNews = {} as NewsResponseDto;
    this.selectedFile = null;
    this.modalService.open(newsAddModal, { ariaLabelledBy: 'modal-basic-title' });
  }

  openExistingNewsModal(id: number, newsAddModal: TemplateRef<any>) {
    this.newsService.find(id).subscribe({
      next: res => {
        this.currentNews = res;
        // this.selectedFile = null;
        this.modalService.open(newsAddModal, { ariaLabelledBy: 'modal-basic-title' });
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  addNews(form) {
    this.submitted = true;

    if (form.valid && this.selectedFile) {
      this.createNews(this.currentNews);
      this.clearForm();
    }
  }

  private clearForm() {
    this.currentNews = {} as NewsResponseDto;
    this.submitted = false;
  }

  getNews(): NewsResponseDto[] {
    return this.news;
  }

  vanishError() {
    this.error = false;
  }

  vanishSuccess() {
    this.success = false;
  }

  private createNews(news: NewsResponseDto) {
    this.newsService.create(news.title, news.summary, news.text, this.selectedFile).subscribe({
      next: () => {
        this.modalService.dismissAll();
        this.loadNews();
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

  loadNews() {
    if (this.newsMode === 'ALL') {
      this.newsService.findAll().subscribe({
        next: (news: NewsResponseDto[]) => {
          this.allNews = news;
          this.news = this.allNews;
        },
        error: error => {
          this.defaultServiceErrorHandling(error);
        }
      });
    } else {
      this.newsService.findUnread().subscribe({
        next: (news: NewsResponseDto[]) => {
          this.unreadNews = news;
          this.news = this.unreadNews;
        },
        error: error => {
          this.defaultServiceErrorHandling(error);
        }
      });
    }
  }

  switchMode(mode: 'ALL' | 'UNREAD') {
    this.newsMode = mode;
    this.news = [];
    this.loadNews();
  }

  private defaultServiceErrorHandling(error: any) {
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
