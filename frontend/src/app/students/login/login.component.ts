import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { StudentService } from '../../services/user/student.service';
import { Router, RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermissionsService } from '../../services/auth/permissions.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  cpr: string = '';
  password: string = '';
  isDisabled: boolean = false;


  constructor(private permissionService: PermissionsService, private toastService: ToastService, private studentService: StudentService, private router: Router) { }

  normalizeNumber(
    input: string | number | undefined,
    requiredLength: number
  ): string | undefined {
    if (input === undefined || input === null) return undefined;

    let str = String(input);

    const arabicToEnglishMap: Record<string, string> = {
      '٠': '0', '١': '1', '٢': '2', '٣': '3', '٤': '4',
      '٥': '5', '٦': '6', '٧': '7', '٨': '8', '٩': '9'
    };

    str = str.replace(/[٠-٩]/g, d => arabicToEnglishMap[d]);

    const regex = new RegExp(`^\\d{${requiredLength}}$`);
    if (regex.test(str)) {
      return str;
    }

    return undefined;
  }

  onSubmit() {
    const normalizedUsername = this.normalizeNumber(this.cpr, 9)
    const normalizedPassword = this.normalizeNumber(this.password, 9)
    if (!this.cpr.trim() || !this.password.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    const body = { username: normalizedUsername, password: normalizedPassword || this.password }
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
        console.log('Router config:', this.router.config);
        this.router.navigate(['/student']).then(success => console.log('Navigation success:', success));
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
