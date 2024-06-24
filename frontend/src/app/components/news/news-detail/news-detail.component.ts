import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {NewsResponseDto, NewsEndpointService, EventDto} from "../../../services/openapi";
import {Location} from '@angular/common';
import {MessagingService} from "src/app/services/messaging.service";

@Component({
    selector: 'app-news-detail',
    templateUrl: './news-detail.component.html',
    styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit {

    newsId: number;
    news: NewsResponseDto & { eventDto?: EventDto } = {
        id: 0,
        title: '',
        summary: '',
        text: '',
        image: [],
        publishedAt: '',
        eventDto: null
    };
    errorMessage = '';
    hover = false;

    constructor(private route: ActivatedRoute,
                private newsServiceNew: NewsEndpointService,
                private router: Router,
                private location: Location,
                private messagingService: MessagingService) {
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

    navigateToEvent(eventId: number): void {
        this.router.navigate(['/event', eventId]);
    }

    getParagraphs(text: string): string[] {
        return text.split('\n');
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

    goBack() {
        this.location.back();
    }
}
