import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { StudentService } from '../../services/user/student.service';
import { PermissionsService } from '../../services/auth/permissions.service';
import { Subscription } from 'rxjs';
import { ToastService } from '../../services/toast.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  permissions = new Map<string, boolean>();
  private subscription!: Subscription;

  constructor(public toastService: ToastService, public permissionsService: PermissionsService, public router: Router, private authService: AuthService) { }

  ngOnInit() {
    if (!environment.production) {
      console.log(this.permissionsService.getPermissions());
      console.log(this.permissionsService.hasPermission('ADMIN'));
    }
    this.subscription = this.permissionsService.permissions$.subscribe(perms => {
      this.permissions = perms;
    });
  }


  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  hasPermission(role: string): boolean {
    return this.permissions.get(role) === true;
  }

  isPermissionsEmpty(): boolean {
    return this.permissions.size === 0;
  }

  isStaffRoute(): boolean {
    return this.router.url.includes('/staff/');
  }


  logout() {
    this.authService.logout().subscribe({
      next: (res) => {
        if (res) {
          this.permissionsService.setPermissions(new Map());
          this.toastService.show(res.message, 'success');
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        if (!environment.production) {
          console.log(error);
        }
        if (error.error.status === "ALGD-500") {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
      }
    });
  }

  
}
