import { TestBed } from '@angular/core/testing';

import { MessagingService } from './messaging.service';

describe('MessageService', () => {
  let service: MessagingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessagingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
