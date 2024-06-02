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
  success = false;
  successMessage = '';

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
    this.selectedFile = null;
    this.modalService.open(newsAddModal, {ariaLabelledBy: 'modal-basic-title'});
  }

  openExistingNewsModal(id: number, newsAddModal: TemplateRef<any>) {
    this.newsServiceNew.find(id).subscribe({
      next: res => {
        this.currentNews = res;
       // this.selectedFile = null;
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

    if (form.valid && this.selectedFile) {
      this.createNews(this.currentNews);
      this.clearForm();

    }
    /*
  else {
    this.errorMessage = 'Bitte fülle alle Felder aus und wähle ein Bild!';
    this.error = true;
  }
  */

  }

  private clearForm() {
    this.currentNews = {} as DetailedNewsDto;
    this.submitted = false;


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
   * Success flag will be deactivated, which clears the success news
   */
  vanishSuccess() {
    this.success = false;
  }




  /**
   * Sends news creation request
   *
   * @param news the news which should be created
   */
  private createNews2(news: DetailedNewsDto) {
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


  private createNews(news: DetailedNewsDto) {
    this.newsServiceNew.create(news.title, news.summary, news.text, this.selectedFile).subscribe({
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
