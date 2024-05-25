import { Component } from '@angular/core';

@Component({
  selector: 'app-show-search',
  templateUrl: './show-search.component.html',
  styleUrl: './show-search.component.scss'
})
export class ShowSearchComponent {
  activeContent: string = 'location';

  setActiveContent(content: string) {
    this.activeContent = content;
  }
}
