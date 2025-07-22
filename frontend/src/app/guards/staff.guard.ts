import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StaffService } from '../services/staff.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { PermissionsService } from '../services/permissions.service';

export const staffGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const staffService = inject(StaffService)
  const permissionService = inject(PermissionsService)

  return staffService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }

        return true;
      }
      const emptyMap = new Map();
      const stringified = JSON.stringify(emptyMap);
      localStorage.setItem('permissions', stringified);
      permissionService.setPermissions(emptyMap);
      router.navigate(['/staff/login'])
      return false;
    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }

      const emptyMap = new Map();
      const stringified = JSON.stringify(emptyMap);
      localStorage.setItem('permissions', stringified);
      permissionService.setPermissions(emptyMap);

      router.navigate(['/staff/login']);
      return of(false);
    })
  )
};
