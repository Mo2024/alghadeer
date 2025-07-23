import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { staffRoleGuard } from './staff-role.guard';

describe('staffRoleGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => staffRoleGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
