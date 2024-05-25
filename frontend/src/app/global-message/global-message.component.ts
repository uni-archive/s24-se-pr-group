import { Component, OnInit } from '@angular/core';
import { MessagingService } from '../services/messaging.service';

@Component({
  selector: 'app-global-message',
  templateUrl: './global-message.component.html',
  styleUrls: ['./global-message.component.scss']
})
export class GlobalMessageComponent implements OnInit {
  messages: { id: number, text: string, type: string }[] = [];
  detailedMessage: string = '';

  constructor(private messageService: MessagingService) {}

  ngOnInit() {
    this.messageService.getMessage().subscribe((message) => {
      if (message) {
        this.messages.unshift({ id: new Date().getTime(), text: message.text, type: message.type });
      }
    });
  }

  closeMessage(id: number) {
    this.messages = this.messages.filter(message => message.id !== id);
  }

  showDetails(text: string) {
    this.detailedMessage = text;
  }

  closeDetails() {
    this.detailedMessage = '';
  }
}
