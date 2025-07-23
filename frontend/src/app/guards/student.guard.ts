import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StudentService } from '../services/student.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { PermissionsService } from '../services/permissions.service';

export const studentGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const router = inject(Router);
  const studentService = inject(StudentService)
  const permissionService = inject(PermissionsService)

  return studentService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }
        const permissionsMap = res as { [key: string]: boolean };

        if (allowedRole === 'STUDENT' && permissionsMap['STUDENT']) {
          return true;
        }

        router.navigate(['/unauthorized']);
        return false;
      } else {
        const stringified = JSON.stringify(new Map());
        localStorage.setItem('permissions', stringified);
        router.navigate(['/login'])
        return false;
      }
    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }
      const emptyMap = new Map();
      const stringified = JSON.stringify(emptyMap);
      permissionService.setPermissions(emptyMap);
      localStorage.setItem('permissions', stringified);
      router.navigate(['/login']);
      return of(false);
    })
  )
};
