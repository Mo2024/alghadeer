import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StaffService } from '../services/staff.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';

export const staffGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const staffService = inject(StaffService)

  return staffService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }

        return true;
      }
      const stringified = JSON.stringify(new Map());
      localStorage.setItem('permissions', stringified);
      router.navigate(['/staff/login'])
      return false;
    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }
      const stringified = JSON.stringify(new Map());
      localStorage.setItem('permissions', stringified);
      router.navigate(['/staff/login']);
      return of(false);
    })
  )
};
