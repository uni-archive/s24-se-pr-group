import {Component, OnInit} from '@angular/core';
import {EventDto, NewsEndpointService, NewsRequestDto} from '../../../services/openapi';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MessagingService} from "src/app/services/messaging.service";
import { Router } from '@angular/router';

@Component({
    selector: 'app-news-create',
    templateUrl: './news-create.component.html',
    styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {
    errorMessage = '';
    submitted = false;
    newsForm: FormGroup;
    selectedFile: File | null = null;
    selectedEvent: EventDto | null = null;

    constructor(
        private formBuilder: FormBuilder,
        private newsService: NewsEndpointService,
        private messagingService: MessagingService,
        private router: Router
    ) {
    }

    ngOnInit() {
        this.newsForm = this.formBuilder.group({
            title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
            summary: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]],
            text: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(10000)]],
            image: [null, Validators.required],
            event: [null, Validators.required]
        });
    }

    get formControls() {
        return this.newsForm.controls;
    }

    onEventSelected(eventDto: EventDto) {
        this.selectedEvent = eventDto;
        this.newsForm.patchValue({event: eventDto});
    }

    onFileSelected(event): void {
        const file = event.target.files[0];
        if (file && file.type.startsWith('image/')) {
            this.selectedFile = file;
            this.newsForm.patchValue({image: file});
        } else {
            this.newsForm.patchValue({image: null});
            this.selectedFile = null;
            this.errorMessage = 'Nur Bilddateien sind erlaubt.';
        }
    }

    addNews(): void {
        this.submitted = true;
        if (this.newsForm.invalid || !this.selectedFile || !this.selectedEvent) {
            return;
        }
        const news = this.newsForm.value;
        this.createNews(news);
    }

    private createNews(news: any): void {
        const newsData: NewsRequestDto = {
            title: news.title,
            summary: news.summary,
            text: news.text,
            eventDto: this.selectedEvent
        };
        this.newsService.create(this.selectedFile, newsData).subscribe({
            next: () => {
                this.messagingService.setMessage('Die News wurde erfolgreich gespeichert.', "success");
                this.resetForm();
                this.router.navigate(['/news']);
            },
            error: error => {
                this.defaultServiceErrorHandling(error);
            }
        });
    }

    resetForm(): void {
        this.newsForm.reset();
        this.selectedFile = null;
        this.selectedEvent = null;
        this.newsForm.patchValue({image: null, event: null});
        this.submitted = false;

        const fileInput = document.getElementById('inputImage') as HTMLInputElement;
        if (fileInput) {
            fileInput.value = '';
        }

        const eventInput = document.querySelector('app-autocomplete-textfield') as any;
        if (eventInput && eventInput.clear) {
            eventInput.clear();
        }
    }

    private defaultServiceErrorHandling(error: any): void {
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
}
