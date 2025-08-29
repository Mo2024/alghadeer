import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { StudentService } from '../../services/user/student.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PermissionsService } from '../../services/auth/permissions.service';
import { MatIconModule } from '@angular/material/icon';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  name: string = '';
  area: string = '';
  cpr: string = '';
  telephone: string = '';
  email: string = '';
  dateOfBirth: Date | null = null;
  image: File | null = null;
  isDisabled: boolean = false;

  selectedFileName: string = '';



  constructor(private permissionService: PermissionsService, private toastService: ToastService, private studentService: StudentService, private router: Router) { }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.image = input.files[0];
      this.selectedFileName = this.image.name;
    } else {
      this.selectedFileName = '';
    }
  }

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
    const normalizedCPR = this.normalizeNumber(this.cpr, 9)
    const normalizedTelephone = this.normalizeNumber(this.telephone, 8)
    if (
      !this.name.trim() || !this.area.trim() ||
      !this.email.trim() || !this.dateOfBirth) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }


    if (normalizedCPR === undefined) {
      this.toastService.show('الرقم الشخصي يجب أن يكون مكونًا من 9 أرقام', "error");
      return;
    }


    if (normalizedTelephone === undefined) {
      this.toastService.show('رقم الهاتف يجب أن يكون مكونًا من 8 أرقام', "error");
      return;
    }

    const dobDate = new Date(this.dateOfBirth);
    const today = new Date();
    if (isNaN(dobDate.getTime()) || dobDate > today) {
      this.toastService.show('يرجى إدخال تاريخ ميلاد صحيح وليس في المستقبل', "error");
      return;
    }

    this.isDisabled = true;


    const formData = new FormData();
    formData.append('name', this.name);
    formData.append('area', this.area);
    formData.append('cpr', normalizedCPR);
    formData.append('telephone', normalizedTelephone);
    formData.append('email', this.email);
    formData.append('dateOfBirth', this.dateOfBirth.toISOString().split('T')[0]);

    if (this.image) {
      formData.append('image', this.image);
    }

    this.studentService.register(formData).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        const permissionsMap = res.object as Map<string, boolean>;
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
