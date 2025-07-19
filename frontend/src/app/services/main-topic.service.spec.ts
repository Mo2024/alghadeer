import { TestBed } from '@angular/core/testing';

import { MainTopicService } from './main-topic.service';

describe('MainTopicService', () => {
  let service: MainTopicService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MainTopicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
