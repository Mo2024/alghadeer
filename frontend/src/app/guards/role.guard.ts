import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StaffService } from '../services/staff.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { StudentService } from '../services/student.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const router = inject(Router);
  const staffService = inject(StaffService)
  const studentService = inject(StudentService)

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
        } else if (allowedRole === 'STUDENT' && permissionsMap['STUDENT']) {
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
