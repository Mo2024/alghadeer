import { TestBed } from '@angular/core/testing';

import { TopicsGroupsService } from './topics-groups.service';

describe('TopicsGroupsService', () => {
  let service: TopicsGroupsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TopicsGroupsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
