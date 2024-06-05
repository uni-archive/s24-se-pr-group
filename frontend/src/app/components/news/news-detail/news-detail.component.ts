import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbPaginationConfig } from "@ng-bootstrap/ng-bootstrap";
import { UntypedFormBuilder } from "@angular/forms";
import { AuthService } from "../../../services/auth.service";
import { NewsResponseDto, NewsEndpointService } from "../../../services/openapi";

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit {
  newsId: number;
  news: NewsResponseDto;
  error = false;
  errorMessage = '';
  constructor(private route: ActivatedRoute,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private modalService: NgbModal,
              private newsServiceNew: NewsEndpointService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.newsId = +params.get('id');
      this.loadNews(this.newsId);
    });
  }

  loadNews(newsId: number): void {
    this.newsServiceNew.find(newsId).subscribe({
      next: res => {
        this.news = res;
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  getParagraphs(text: string): string[] {
    return text.split('\n');
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

}
