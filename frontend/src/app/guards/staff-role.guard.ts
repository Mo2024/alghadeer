import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { StaffService } from '../services/staff.service';
import { inject } from '@angular/core';

export const staffRoleGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const router = inject(Router);
  const staffService = inject(StaffService)

  return staffService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }
        const permissionsMap = res as { [key: string]: boolean };
        if (allowedRole === 'INSTRUCTOR' && (permissionsMap['INSTRUCTOR'] || permissionsMap['SUPERVISOR'] || permissionsMap['ADMIN'])) {
          return true;
        } else if (allowedRole === 'SUPERVISOR' && (permissionsMap['SUPERVISOR'] || permissionsMap['ADMIN'])) {
          return true;
        } else if (allowedRole === 'ADMIN' && permissionsMap['ADMIN']) {
          return true;
        }

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
