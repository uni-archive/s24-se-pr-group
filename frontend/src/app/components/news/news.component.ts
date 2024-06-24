import {Component, OnInit} from '@angular/core';
import {SimpleNewsResponseDto, NewsEndpointService} from "../../services/openapi";
import {AuthService} from '../../services/auth.service';
import {MessagingService} from "src/app/services/messaging.service";

@Component({
    selector: 'app-news',
    templateUrl: './news.component.html',
    styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {
    errorMessage = '';
    news: SimpleNewsResponseDto[] = [];
    newsMode: 'ALL' | 'UNREAD' = 'ALL';
    totalElements: number = 0;
    pageSize: number = 9;
    currentPage: number = 1;

    constructor(
        private newsService: NewsEndpointService,
        private messagingService: MessagingService,
        public authService: AuthService
    ) {
    }

    ngOnInit() {
        if (this.authService.isLoggedIn()) {
            this.newsMode = 'UNREAD';

        } else {
            this.newsMode = 'ALL';
        }
        this.loadNews();
    }

    getNews(): SimpleNewsResponseDto[] {
        return this.news;
    }

    loadNews() {
        const page = this.currentPage - 1;
        const size = this.pageSize;

        if (this.newsMode === 'ALL') {
            this.newsService.findAll(page, size).subscribe({
                next: (response: any) => {
                    this.news = response.content;
                    this.totalElements = response.totalElements;
                },
                error: error => {
                    this.defaultServiceErrorHandling(error);
                }
            });
        } else {
            this.newsService.findUnread(page, size).subscribe({
                next: (response: any) => {
                    this.news = response.content;
                    this.totalElements = response.totalElements;
                },
                error: error => {
                    this.defaultServiceErrorHandling(error);
                }
            });
        }
    }

    switchMode(mode: 'ALL' | 'UNREAD') {
        this.newsMode = mode;
        this.currentPage = 1;
        this.loadNews();
    }

    private defaultServiceErrorHandling(error: any) {
        console.log(error);
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
        this.messagingService.setMessage(this.errorMessage, "danger");
    }

    onPageChange(page: number) {
        this.currentPage = page;
        this.loadNews();
    }
}
