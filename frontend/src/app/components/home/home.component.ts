import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {
    SimpleNewsResponseDto,
    NewsEndpointService,
    EventWithTicketCountDto,
    EventEndpointService
} from '../../services/openapi';
import {Chart, ChartEvent, registerables} from 'chart.js';

Chart.register(...registerables);

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
    news: SimpleNewsResponseDto[] = [];
    events: EventWithTicketCountDto[] = [];
    itemsPerPage: number = 3;

    @ViewChild('scrollContainer', {static: false}) scrollContainer: ElementRef;

    constructor(
        public authService: AuthService,
        private newsService: NewsEndpointService,
        private eventService: EventEndpointService,
        private router: Router
    ) {
    }

  ngOnInit() {
    if (this.authService.isLoggedIn() ) {
      this.loadUnreadNews()
    } else {
      this.loadAllNews();
    }
    this.loadEvents();
  }

    loadAllNews() {
        this.newsService.findAll(0, 15).subscribe({
            next: (response: any) => {
                this.news = response.content;
            },
            error: error => {
                console.error('Error loading news', error);
            }
        });
    }

    loadUnreadNews() {
        this.newsService.findUnread(0, 15).subscribe({
            next: (response: any) => {
                this.news = response.content;
            },
            error: error => {
                console.error('Error loading news', error);
            }
        });
    }

    loadEvents() {
        this.eventService.getTop10EventsWithMostTickets().subscribe({
            next: (response: any) => {
                this.events = response;
                this.createChart();
            },
            error: error => {
                console.error('Error loading events', error);
            }
        });
    }

    createChart() {
        const eventTitles = this.events.map(event => event.title);
        const ticketCounts = this.events.map(event => event.ticketCount);

        const ctx = (document.getElementById('eventChart') as HTMLCanvasElement).getContext('2d');
        const chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: eventTitles,
                datasets: [{
                    label: 'Ticketverkäufe in den letzten 30 Tagen',
                    data: ticketCounts,
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgb(16,116,189)',
                    borderWidth: 1
                }]
            },
            options: {
                indexAxis: 'y',
                scales: {
                    x: {
                        beginAtZero: true
                    },
                    y: {
                        ticks: {
                            align: 'start',
                            font: {
                                size: 16,
                                weight: 'bold'
                            },
                            color: 'black'
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        enabled: true,
                        callbacks: {
                            label: (context) => {
                                const event = this.events[context.dataIndex];
                                return ` ${event.title}: ${event.ticketCount} Tickets`;
                            }
                        }
                    }
                },
                onClick: (event: ChartEvent, elements: any[]) => {
                    if (elements.length > 0) {
                        const elementIndex = elements[0].index;
                        const eventId = this.events[elementIndex].eventId;
                        this.router.navigate(['/event', eventId]);
                    }
                }
            }
        });

        (chart.canvas as HTMLCanvasElement).addEventListener('mousemove', (event) => {
            const points = chart.getElementsAtEventForMode(event, 'nearest', {intersect: true}, true);
            if (points.length) {
                (chart.canvas as HTMLCanvasElement).style.cursor = 'pointer';
            } else {
                (chart.canvas as HTMLCanvasElement).style.cursor = 'default';
            }
        });

        chart.update();
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
            container.scrollBy({left: scrollAmount, behavior: 'smooth'});
        } else {
            container.scrollBy({left: -scrollAmount, behavior: 'smooth'});
        }
    }
}
