import { Component } from '@angular/core';
import { PermissionsService } from '../../../services/permissions.service';
import { ToastService } from '../../../services/toast.service';
import { StaffService } from '../../../services/staff.service';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  email: string = '';
  name: '' | 'ADMIN' | 'SUPERVISOR' | 'INSTRUCTOR' = '';
  isDisabled: boolean = false;
  role: string = ''


  constructor(private toastService: ToastService, private staffService: StaffService, private router: Router) { }


  onSubmit() {
    if (!this.email.trim() || !this.name.trim() || !this.role.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    const body = {
      email: this.email,
      name: this.name,
      permissions: [
        {
          permission: this.role
        }
      ]
    }
    this.staffService.register(body).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        this.email = '';
        this.name = '';
        this.role = '';

        this.toastService.show('تم تسجيل الطاقم بنجاح', 'success');
      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }

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
