import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StaffService } from '../services/staff.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { StudentService } from '../services/student.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const allowedRoles: string[] = route.data['roles'];
  const router = inject(Router);
  const staffService = inject(StaffService)
  const studentService = inject(StudentService)

  return staffService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }
        const permissionsMap = res as Map<string, boolean>;
        const hasAccess = Array.from(permissionsMap.keys()).some(role => allowedRoles.includes(role) && permissionsMap.get(role) === true);
        if (permissionsMap.get('STUDENT')) {
          studentService.setPermissions(permissionsMap);
        } else if (permissionsMap.get('ADMIN') || permissionsMap.get('SUPERVISOR') || permissionsMap.get('INSTRUCTOR')) {
          staffService.setPermissions(permissionsMap);
        }

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
      staffService.setPermissions(new Map());
      router.navigate(['/unauthorized']);
      return of(false);
    })
  )
};
