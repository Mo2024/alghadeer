import { TestBed } from '@angular/core/testing';

import { SalahService } from './salah.service';

describe('SalahService', () => {
  let service: SalahService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SalahService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
