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
  messageDisplayDuration: number = 15000; // Duration in milliseconds

  constructor(private messageService: MessagingService) {}

  ngOnInit() {
    this.messageService.getMessage().subscribe((message) => {
      if (message) {
        const newMessage = { id: new Date().getTime(), text: message.text, type: message.type };
        this.messages.unshift(newMessage);
        this.setAutoClose(newMessage.id);
      }
    });
  }

  setAutoClose(messageId: number) {
    setTimeout(() => {
      this.closeMessage(messageId);
    }, this.messageDisplayDuration);
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
