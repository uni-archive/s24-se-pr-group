import {ChangeDetectorRef, Component, OnInit, TemplateRef} from '@angular/core';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {DetailedNewsDto, NewsEndpointService, SimpleNewsDto} from "../../services/openapi";

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  currentNews: DetailedNewsDto;

  private news: SimpleNewsDto[];

  selectedFile: File;


  constructor(private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private modalService: NgbModal,
              private newsServiceNew: NewsEndpointService) {
  }

  ngOnInit() {
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
    this.currentNews = {} as DetailedNewsDto;
    this.modalService.open(newsAddModal, {ariaLabelledBy: 'modal-basic-title'});
  }

  openExistingNewsModal(id: number, newsAddModal: TemplateRef<any>) {
    this.newsServiceNew.find(id).subscribe({
      next: res => {
        this.currentNews = res;
        this.modalService.open(newsAddModal, {ariaLabelledBy: 'modal-basic-title'});
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  /**
   * Starts form validation and builds a news dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addNews(form) {
    this.submitted = true;

    if (form.valid) {
      this.createNews(this.currentNews);
      this.clearForm();
    }
  }

  getNews(): DetailedNewsDto[] {
    return this.news;
  }

  /**
   * Error flag will be deactivated, which clears the error news
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Sends news creation request
   *
   * @param news the news which should be created
   */
  private createNews(news: DetailedNewsDto) {
    console.log('NewsDto1:', news);


    this.newsServiceNew.create(news.title, news.summary, news.text, this.selectedFile).subscribe({
      next: () => {
        this.loadNews();
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }


  /**
   * Loads the specified page of news from the backend
   */
  private loadNews() {
    this.newsServiceNew.findAll()
    .subscribe({
      next: (news: SimpleNewsDto[]) => {
        this.news = news;
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.currentNews = {} as DetailedNewsDto;
    this.submitted = false;
  }

}
