import { Component } from '@angular/core';
import { SessionService } from '../../../../services/semester/session.service';
import { environment } from '../../../../../environments/environment';
import { ToastService } from '../../../../services/toast.service';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.css'
})
export class AttendanceComponent {

  constructor(private sessionService: SessionService, private toastService: ToastService, private route: ActivatedRoute) { }


  sessionId!: number;
  classId!: number;
  statusObject: any;

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.sessionId = +params['sessionId']; // '+' converts to number
      this.classId = +params['classId'];
      console.log('Session ID:', this.sessionId);
      console.log('Class ID:', this.classId);
    });

    this.sessionService.getAttendanceStatus({
      sessionId: this.sessionId,
      classId: this.classId
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.statusObject = res;
          if (res.students && res.students.length === 0) this.toastService.show("لا يوجد طلبة في هذا الصف", 'error')
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
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

  submitAttendance() {
    const body = {
      attendances: this.statusObject.students,
      session: {
        id: this.sessionId,
        class: {
          id: this.classId
        }
      }
    }

    this.sessionService.takeAttendance(body).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.statusObject = res;
          this.toastService.show("تم تسجيل الحضور بنجاح", 'success');

        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
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
