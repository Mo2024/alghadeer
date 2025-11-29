import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../services/toast.service';
import { ClassService } from '../../services/semester/class.service';
import { StudentService } from '../../services/user/student.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SalahService } from '../../services/salah.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-salah',
  imports: [CommonModule, FormsModule],
  templateUrl: './salah.component.html',
  styleUrl: './salah.component.css'
})
export class SalahComponent {

  students: any = []
  studentLevel: any = ''
  classId: any = '';
  studentId: any = '';
  classes: any[] = [];
  attempts: any[] = [];
  constructor(
    private toastService: ToastService,
    private salahService: SalahService,
    private classService: ClassService,
    private studentService: StudentService,
    private router: Router) { }

  ngOnInit() {

    this.attempts = []

    const state = history.state;
    let attemptReroute = state.attemptReroute;

    if (attemptReroute) {
      this.onClassChange(state.classId)
      this.onStudentChange(state.studentId)
      this.studentId = state.studentId;
      this.classId = state.classId;
      state.attemptReroute = null;
      state.studentId = null
      state.classId = null
    }

    this.classService.getClassesByLatestSemester().subscribe({
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

  createAttempt() {
    if (this.studentId == '') {
      this.toastService.show("يرجى اختيار طالب أولاً", 'error');
      return
    }

    this.router.navigate(['/staff/salah/attempt'], { queryParams: { studentId: this.studentId, new: true, classId: this.classId } });

  }

  onClassChange(value: any) {
    this.studentId = '';
    this.studentLevel = ''
    this.attempts = []
    this.studentService.getStudentsByClassId(value).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
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

  onStudentChange(value: any) {
    this.studentLevel = ''
    this.salahService.getStudentAttempt(value).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.attempts = res;
          this.getLevelByStudentId(value);
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

  continueAttempt(attemptId: number) {
    this.router.navigate(['/staff/salah/attempt'], { queryParams: { attemptId: attemptId, new: false, studentId: this.studentId, classId: this.classId } });
  }

  getLevelByStudentId(studentId: any) {
    this.salahService.getLevelByStudentId(studentId).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.studentLevel = res.level

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

  onLevelChange(value: any) {

    this.salahService.updateLevel({
      id: null,
      level: value,
      student: { id: this.studentId }
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.toastService.show(res.message)
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
