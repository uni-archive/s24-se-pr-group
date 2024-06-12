import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {NewsResponseDto, NewsEndpointService, EventDto} from "../../../services/openapi";

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
    error = false;
    errorMessage = '';

    constructor(private route: ActivatedRoute,
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
                console.log('API Response:', res);
                if (!res.hasOwnProperty('eventDto')) {
                    console.warn('eventDto field is missing in the API response!');
                } else if (res.eventDto === null) {
                    console.warn('eventDto is null! API Response:', res);
                }
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
