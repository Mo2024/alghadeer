import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { StudentService } from '../../services/user/student.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermissionsService } from '../../services/auth/permissions.service';

@Component({
    selector: 'app-login',
    imports: [CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {

  cpr: string = '';
  password: string = '';
  isDisabled: boolean = false;


  constructor(private permissionService: PermissionsService, private toastService: ToastService, private studentService: StudentService, private router: Router) { }


  onSubmit() {
    if (!this.cpr.trim() || !this.password.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    const body = { username: this.cpr, password: this.password }
    this.studentService.login(body).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        const permissionsMap = res.object as Map<string, boolean>;
        console.log(permissionsMap)
        this.permissionService.setPermissions(permissionsMap)


        this.toastService.clear()
        this.router.navigate(['/student']);
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
