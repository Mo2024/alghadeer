import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { ToastComponent } from '../../components/toast/toast.component';
import { StaffService } from '../../services/staff.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  isDisabled: boolean = false;


  constructor(private toastService: ToastService, private staffService: StaffService, private router: Router) { }


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

        this.toastService.clear()
        this.router.navigate(['/']);
      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }
        console.log(error.error.status)
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
