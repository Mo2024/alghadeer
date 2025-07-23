import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { PermissionsService } from '../services/permissions.service';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const accessControlled: boolean = route.data['accessControlled'];
  const router = inject(Router);
  const authService = inject(AuthService)
  const permissionService = inject(PermissionsService)

  return authService.getAuth().pipe(
    map((res: any) => {
      if (accessControlled) {
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

          permissionService.setPermissions(res);

          router.navigate(['/unauthorized']);
          return false;
        } else {
          permissionService.setPermissions(new Map());
          if (!['/staff/login', '/login'].includes(state.url)) {
            if (state.url.includes('/staff/')) {
              router.navigate(['/staff/login']);
            } else {
              router.navigate(['/login']);
            }
          } else return true
          return false;
        }
      } else {
        if (res) {
          const permissionsMap = res as Map<string, boolean>;
          permissionService.setPermissions(permissionsMap);
          return true;
        } else {
          permissionService.setPermissions(new Map());
          return true;
        }
      }

    }),
    catchError((error) => {
      if (!environment.production) {
        console.log(error)
      }

      permissionService.setPermissions(new Map());

      if (!['/staff/login', '/login'].includes(state.url)) {
        if (state.url.includes('/staff/')) {
          router.navigate(['/staff/login']);
        } else {
          router.navigate(['/login']);
        }
      } else return of(true)

      return of(false);
    })
  )
};
