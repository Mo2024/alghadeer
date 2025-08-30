import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const mainPageGuardGuard: CanActivateFn = (route, state) => {
  const hostname = window.location.hostname;
  const router = inject(Router);


  if (hostname.startsWith('staff.')) {
    router.navigate(['/staff/instructor/assigned-classes']);
    return false;
  }
  if (hostname.startsWith('stu.')) {
    router.navigate(['/student']);
    return false;
  }

  return true
};
