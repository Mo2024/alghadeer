import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { StudentService } from '../../services/student.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment.development';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermissionsService } from '../../services/permissions.service';

@Component({
  selector: 'app-login',
  standalone: true,
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
        const stringified = JSON.stringify(permissionsMap);
        localStorage.setItem('permissions', stringified);
        this.permissionService.setPermissions(permissionsMap)


        this.toastService.clear()
        this.router.navigate(['/student']);
      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }

        const emptyMap = new Map();
        const stringified = JSON.stringify(emptyMap);
        this.permissionService.setPermissions(emptyMap);

        localStorage.setItem('permissions', stringified);
        if (error.error.status === 400) {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === 403) {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === 500) {
          this.toastService.show(error.error.message, 'error');
        } else {
          this.toastService.show(error.error.message || "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
        this.isDisabled = false;
      }
    })

  }

}
