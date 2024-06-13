import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { SimpleNewsResponseDto, NewsEndpointService } from '../../services/openapi';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  news: SimpleNewsResponseDto[] = [];
  itemsPerPage: number = 3;

  @ViewChild('scrollContainer', { static: false }) scrollContainer: ElementRef;

  constructor(
      public authService: AuthService,
      private newsService: NewsEndpointService
  ) {}

  ngOnInit() {
    this.loadNews();
  }

  loadNews() {
    this.newsService.findUnread(0, 9).subscribe({
      next: (response: any) => {
        this.news = response.content;
      },
      error: error => {
        console.error('Error loading news', error);
      }
    });
  }

  nextNews() {
    this.scroll('next');
  }

  prevNews() {
    this.scroll('prev');
  }

  scroll(direction: 'next' | 'prev') {
    const container = this.scrollContainer.nativeElement;
    const cardWidth = container.querySelector('.col-4').clientWidth + 30;
    const scrollAmount = this.itemsPerPage * (cardWidth - 30);

    if (direction === 'next') {
      container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    } else {
      container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    }
  }
}
