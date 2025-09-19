import { Component } from '@angular/core';
import { ToastService } from '../../../services/toast.service';
import { StaffService } from '../../../services/user/staff.service';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-update-password',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-password.component.html',
  styleUrl: './update-password.component.css'
})
export class UpdatePasswordComponent {

  isDisabled: boolean = false;
  updatePasswordReq: any = {
    currentPassword: '',
    newPassword: '',
    newPassword2: ''
  };

  constructor(private toastService: ToastService, private staffService: StaffService) { }


  onSubmit() {

    if (!this.updatePasswordReq.currentPassword?.trim()) {
      this.toastService.show("يرجى التأكد من إدخال الكلمة السرية الحالية بشكل صحيح", "error");
      return;
    }

    if (!this.updatePasswordReq.newPassword?.trim()) {
      this.toastService.show("يرجى التأكد من إدخال الكلمة السرية الجديدة بشكل صحيح", "error");
      return;
    }

    if (!this.updatePasswordReq.newPassword2?.trim()) {
      this.toastService.show("يرجى التأكد من إدخال الكلمة السرية الجديدة الثانية بشكل صحيح", "error");
      return;
    }

    if (this.updatePasswordReq.newPassword.length < 6 || this.updatePasswordReq.newPassword2.length < 6) {
      this.toastService.show("كلمة السر يجب أن تتكون من 6 أحرف على الأقل", "error");
      return;
    }

    if (this.updatePasswordReq.newPassword !== this.updatePasswordReq.newPassword2) {
      this.toastService.show("كلمتا السر غير متطابقتين، يرجى إعادة المحاولة", "error");
      return;
    }

    this.isDisabled = true;
    this.staffService.updatePassword(this.updatePasswordReq).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        this.updatePasswordReq = {
          currentPassword: '',
          newPassword: '',
          newPassword2: ''
        };
        this.toastService.show(res.message, 'success');

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
