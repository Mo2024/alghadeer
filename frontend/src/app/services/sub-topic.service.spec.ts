import { TestBed } from '@angular/core/testing';

import { SubTopicService } from './sub-topic.service';

describe('SubTopicService', () => {
  let service: SubTopicService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubTopicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
