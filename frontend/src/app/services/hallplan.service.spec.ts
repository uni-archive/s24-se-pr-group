import { TestBed } from '@angular/core/testing';

import { HallplanService } from './hallplan.service';

describe('HallplanService', () => {
  let service: HallplanService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HallplanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
