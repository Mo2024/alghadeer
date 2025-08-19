import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PermissionsService } from '../../services/auth/permissions.service';
import { ToastService } from '../../services/toast.service';
import { StaffService } from '../../services/user/staff.service';
import { environment } from '../../../environments/environment.development';
@Component({
    selector: 'app-login',
    imports: [CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  isDisabled: boolean = false;


  constructor(private permissionService: PermissionsService, private toastService: ToastService, private staffService: StaffService, private router: Router) { }


  ngOnInit() {

  }

  onSubmit() {
    if (!this.email.trim() || !this.password.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    const body = { username: this.email, password: this.password }
    this.staffService.login(body).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        const permissionsMap = res.object as Map<string, boolean>;
        this.permissionService.setPermissions(permissionsMap);

        this.toastService.clear()
        this.router.navigate(['/']);
      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }
        this.permissionService.setPermissions(new Map());
        if (error.error.status === "ALGD-400") {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === "ALGD-403") {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === "ALGD-500") {
          this.toastService.show(error.error.message, 'error');
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
        this.isDisabled = false;
      }
    })

  }

}
