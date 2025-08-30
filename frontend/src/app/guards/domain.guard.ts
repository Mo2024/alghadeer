import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const domainGuard: CanActivateFn = (route, state) => {

  const hostname = window.location.hostname;
  const router = inject(Router);


  if (state.url.startsWith('/staff') && !hostname.startsWith('staff.')) {
    router.navigate(['/']);
    return false;
  }
  if (!hostname.startsWith('stu.') && (state.url.startsWith('/student') || state.url.startsWith('/login') || state.url.startsWith('/register'))) {
    router.navigate(['/']);
    return false;
  }

  return true
};
