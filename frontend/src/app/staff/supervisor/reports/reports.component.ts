import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { ReportsService } from '../../../services/supervisor/reports.service';
import { environment } from '../../../../environments/environment';
import { SemesterService } from '../../../services/semester/semester.service';

@Component({
  selector: 'app-reports',
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent {

  report: string = '';
  isDisabled: boolean = false;
  semesters: any = [];
  semesterId = '';

  constructor(private toastService: ToastService, private reportService: ReportsService, private semesterService: SemesterService) { }


  onSubmit() {
    this.isDisabled = true;

    this.reportService.getEnrolledStudentsTelephone(this.semesterId).subscribe({
      next: (res: Blob) => {
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'enrolled_students.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);

        this.toastService.show('تم تنزيل التقرير بنجاح', 'success');
        this.isDisabled = false;
      },
      error: (error) => {
        this.isDisabled = false;
        if (!environment.production) console.log(error);
        this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
      }
    });
  }


  onReportChange(value: string) {
    if (value === 'PHONE_NUMBERS_CLASSES') {
      this.getLatestThreeSemesters();
    }
  }

  getLatestThreeSemesters() {
    this.semesterService.getLatestThreeSemesters().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.semesters = [...res];
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
