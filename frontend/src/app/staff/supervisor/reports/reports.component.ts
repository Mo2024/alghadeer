import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { ReportsService } from '../../../services/supervisor/reports.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-reports',
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent {

  report: string = '';
  isDisabled: boolean = false;

  constructor(private toastService: ToastService, private reportService: ReportsService) { }


  onSubmit() {
    this.isDisabled = true;

    this.reportService.getEnrolledStudentsTelephone().subscribe({
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

}
