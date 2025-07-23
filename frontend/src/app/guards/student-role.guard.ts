import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { inject } from '@angular/core';
import { StudentService } from '../services/student.service';

export const studentRoleGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const router = inject(Router);
  const studentService = inject(StudentService)

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
