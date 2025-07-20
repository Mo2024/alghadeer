import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StudentService } from '../services/student.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';

export const studentGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const studentService = inject(StudentService)

  return studentService.getAuth({}).pipe(
    map((res: any) => {
      if (res) {
        if (!environment.production) {
          console.log(res);
        }
        return true;
      }
      router.navigate(['/student/login'])
      return false;
    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }
      router.navigate(['/student/login']);
      return of(false);
    })
  )
};
