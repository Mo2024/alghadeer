import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { PermissionsService } from '../services/auth/permissions.service';
import { AuthService } from '../services/auth/auth.service';


export const authGuard: CanActivateFn = (route, state) => {
  const allowedRole: string = route.data['role'];
  const accessControlled: boolean = route.data['accessControlled'];
  const router = inject(Router);
  const authService = inject(AuthService)
  const permissionService = inject(PermissionsService)
  const hostname = window.location.hostname;


  return authService.getAuth().pipe(
    map((res: any) => {
      console.log('123456777')
      if (accessControlled) {
        if (res) {
          permissionService.setPermissions(res);

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
        } else {
          permissionService.setPermissions(new Map());
          if (!['/staff/login', '/login'].includes(state.url)) {

            if (state.url.includes('/staff/')) {
              router.navigate(['/staff/login']);
            } else {
              sessionStorage.removeItem('redirectUrl');

              router.navigate(['/login']);
            }
          } else {

            return true
          }
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
      console.log(hostname)
      permissionService.setPermissions(new Map());

      if (!['/staff/login', '/login'].includes(state.url) && accessControlled) {
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
