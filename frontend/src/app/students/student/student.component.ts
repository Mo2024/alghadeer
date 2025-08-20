import { Component } from '@angular/core';
import { SemesterService } from '../../services/semester/semester.service';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxGaugeModule } from 'ngx-gauge';

@Component({
  selector: 'app-student',
  imports: [CommonModule, FormsModule, NgxGaugeModule],
  templateUrl: './student.component.html',
  styleUrl: './student.component.css'
})
export class StudentComponent {

  displayedEnrollment: boolean = false;
  grade: string = '';
  isDisabled: boolean = false;


  attendanceValue = 50; // 75% attendance
  gaugeLabel = 'نسبة حضور الفصل';
  gaugeAppendText = '%';

  constructor(private semesterService: SemesterService, private toastService: ToastService) { }

  ngOnInit() {
    this.semesterService.isEnrolled().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.displayedEnrollment = false;
        } else if (!res) {
          this.displayedEnrollment = true
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

  enrollToSemester() {
    if (!this.grade.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    this.semesterService.enrollStudent(this.grade).subscribe({
      next: async (res) => {
        this.displayedEnrollment = false;
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        this.toastService.show('تم التسجيل في الفصل الدراسي بنجاح', 'success');

        this.toastService.clear()
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


  // Function to convert Western digits to Arabic-Indic digits
  getArabicNumber(num: number): string {
    const arabicDigits = '٠١٢٣٤٥٦٧٨٩';
    return num.toString().replace(/\d/g, d => arabicDigits[+d]);
  }
  // Use a getter for the gauge label with number
  get attendanceLabel(): string {
    return `${this.getArabicNumber(this.attendanceValue)}%`;
  }

  get attendanceColor(): string {
    if (this.attendanceValue < 50) return '#e74c3c';   // red
    if (this.attendanceValue < 75) return '#f39c12';   // amber
    return '#1abc9c';                                  // green
  }

}
