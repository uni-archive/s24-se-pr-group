import { Component } from '@angular/core';
import {AppModule} from "../../../app.module";

@Component({
  selector: 'app-show-search',
  templateUrl: './show-search.component.html',
  standalone: true,
  imports: [
    AppModule
  ],
  styleUrl: './show-search.component.scss'
})
export class ShowSearchComponent {
  activeContent: string = 'location';

  setActiveContent(content: string) {
    this.activeContent = content;
  }
}
