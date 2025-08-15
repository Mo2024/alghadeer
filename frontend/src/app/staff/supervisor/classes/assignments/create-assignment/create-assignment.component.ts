import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../../services/toast.service';
import { AssignmentService } from '../../../../../services/semester/assignment.service';
import { ClassService } from '../../../../../services/semester/class.service';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-create-assignment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-assignment.component.html',
  styleUrl: './create-assignment.component.css'
})
export class CreateAssignmentComponent {
  isDisabled: boolean = false;
  classId = ''

  constructor(private toastService: ToastService, private assignmentService: AssignmentService, private classService: ClassService) { }

  activity = {
    name: '',
    startDateTime: '',
    endDateTime: '',
    totalGrade: null
  };

  classes: any

  ngOnInit() {
    this.classService.getActiveClasses().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.classes = res;
        }

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
      }
    })
  }

  onSubmit() {

    if (this.classId == '' || this.classId === '') {
      this.toastService.show('الرجاء إدخال اسم الصف', 'error');
      return
    }

    if (!this.activity.name || this.activity.name.trim() === '') {
      this.toastService.show('الرجاء إدخال اسم النشاط', 'error');
      return;
    }

    if (!this.activity.startDateTime) {
      this.toastService.show('الرجاء تحديد تاريخ ووقت البداية', 'error');
      return;
    }

    if (!this.activity.endDateTime) {
      this.toastService.show('الرجاء تحديد تاريخ ووقت النهاية', 'error');
      return;
    }

    if (new Date(this.activity.startDateTime) > new Date(this.activity.endDateTime)) {
      this.toastService.show('تاريخ البداية يجب أن يكون قبل تاريخ النهاية', 'error');
      return;
    }

    if (this.activity.totalGrade == null || this.activity.totalGrade < 0) {
      this.toastService.show('الرجاء إدخال درجة كلية صحيحة', 'error');
      return;
    }
    this.isDisabled = true;

    this.assignmentService.createAssignment(this.activity, this.classId).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.isDisabled = false;
          this.activity.name = ''
          this.activity.startDateTime = '';
          this.activity.endDateTime = ''
          this.activity.totalGrade = null
          this.classId = ''
          this.toastService.show('تم إنشاء النشاط بنجاح', 'success');

        }

      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }
        this.isDisabled = false;

        if (error.error.status === "ALGD-400") {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === "ALGD-403") {
          this.toastService.show(error.error.message, 'error');
        } else if (error.error.status === "ALGD-500") {
          this.toastService.show(error.error.message, 'error');
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
      }
    })

  }
}
