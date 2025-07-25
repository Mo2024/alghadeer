import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class PermissionsService {

  private permissionsSubject = new BehaviorSubject<Map<string, boolean>>(new Map());

  permissions$ = this.permissionsSubject.asObservable();

  setPermissions(permissions: Map<string, boolean>) {
    const map = new Map<string, boolean>(Object.entries(permissions));
    this.permissionsSubject.next(map);
  }

  getPermissions(): Map<string, boolean> {
    return this.permissionsSubject.getValue();
  }

  hasPermission(permission: string): boolean {
    return this.getPermissions().get(permission) === true;
  }

  isPermissionsEmpty(): boolean {
    return this.permissionsSubject.getValue().size === 0;
  }

}
