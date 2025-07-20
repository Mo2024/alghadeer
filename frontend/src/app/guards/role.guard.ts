import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StaffService } from '../services/staff.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment.development';

export const roleGuard: CanActivateFn = (route, state) => {
  const allowedRoles: string[] = route.data['roles'];
  const router = inject(Router);
  const staffService = inject(StaffService)

  return staffService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }
        const hasAccess = res.some((r: string) => allowedRoles.includes(r));
        if (hasAccess) return true;

        router.navigate(['/unauthorized']);
        return false;
      }
      router.navigate(['/unauthorized']);
      return false;
    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }
      router.navigate(['/unauthorized']);
      return of(false);
    })
  )
};
