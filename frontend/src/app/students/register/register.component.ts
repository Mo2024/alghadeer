import { Component } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { StudentService } from '../../services/student.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PermissionsService } from '../../services/permissions.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  name: string = '';
  area: string = '';
  cpr: number | undefined;
  telephone: number | undefined;
  email: string = '';
  dateOfBirth: string = '';
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


  onSubmit() {
    if (
      !this.name.trim() || !this.area.trim() ||
      !this.email.trim() || !this.dateOfBirth.trim() ||
      this.cpr === undefined || this.telephone === undefined) {

      console.log(this.name)
      console.log(this.area)
      console.log(this.email)
      console.log(this.dateOfBirth)
      console.log(this.cpr)
      console.log(this.telephone)

      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }

    if (this.cpr.toString().length !== 9) {
      this.toastService.show('الرقم الشخصي يجب أن يكون مكونًا من 9 أرقام', "error");
      return;
    }

    if (this.telephone.toString().length !== 8) {
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
    formData.append('cpr', String(this.cpr));
    formData.append('telephone', String(this.telephone));
    formData.append('email', this.email);
    formData.append('dateOfBirth', this.dateOfBirth);

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
