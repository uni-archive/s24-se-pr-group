import {ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild, ViewChildren} from '@angular/core';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, NgForm} from '@angular/forms';
import {AuthService} from '../../services/auth.service';

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

  currentNews: News;

  private news: News[];

  selectedFile: File;


  constructor(private newsService: NewsService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private modalService: NgbModal) {
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
    this.currentNews = new News();
    this.modalService.open(newsAddModal, {ariaLabelledBy: 'modal-basic-title'});
  }

  openExistingNewsModal(id: number, newsAddModal: TemplateRef<any>) {
    this.newsService.getNewsById(id).subscribe({
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
      this.currentNews.image = this.selectedFile;
      this.createNews(this.currentNews);
      this.clearForm();
    }
  }

  getNews(): News[] {
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
  private createNews(news: News) {

    console.log('NewsDto1:', news);



    const formData = new FormData();
    formData.append('title', news.title);
    formData.append('summary', news.summary);
    formData.append('text', news.text);
    formData.append('image', news.image);


    formData.forEach((value, key) => {
      console.log(key, value);
    });

    this.newsService.createNews(formData).subscribe({
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
    this.newsService.getNews().subscribe({
      next: (news: News[]) => {
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
    this.currentNews = new News();
    this.submitted = false;
  }

}
