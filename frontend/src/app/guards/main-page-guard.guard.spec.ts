import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { mainPageGuardGuard } from './main-page-guard.guard';

describe('mainPageGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => mainPageGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
