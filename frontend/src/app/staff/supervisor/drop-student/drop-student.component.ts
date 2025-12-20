import { Component } from '@angular/core';
import { ClassService } from '../../../services/semester/class.service';
import { StudentService } from '../../../services/user/student.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import { ToastService } from '../../../services/toast.service';
import { SemesterService } from '../../../services/semester/semester.service';

@Component({
  selector: 'app-drop-student',
  imports: [CommonModule, FormsModule],
  templateUrl: './drop-student.component.html',
  styleUrl: './drop-student.component.css'
})
export class DropStudentComponent {

  students: any = [];
  classes: any = [];
  classId = '';
  studentId = '';

  isDisabled: boolean = false;



  constructor(private classService: ClassService, private studentService: StudentService, private toastService: ToastService, private semesterService: SemesterService) { }

  ngOnInit() {
    this.classService.getClassesByActiveSemester().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.classes = [...res];
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

  onClassChange(value: string) {
    console.log(value)
    this.studentService.getActiveStudentsByClassId(value).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.studentId = '';
          this.students = [...res];
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

    if (this.studentId == '') {
      this.toastService.show('يرجى اختيار طالب للانسحاب', 'error');
    }

    this.semesterService.dropStudent(this.studentId).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.classId = '';
          this.studentId = '';
          this.toastService.show(res.message, 'success');
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

}
