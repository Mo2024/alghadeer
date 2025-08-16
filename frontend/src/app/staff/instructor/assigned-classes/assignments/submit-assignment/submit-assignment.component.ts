import { Component } from '@angular/core';
import { AssignmentService } from '../../../../../services/semester/assignment.service';
import { ToastService } from '../../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-submit-assignment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './submit-assignment.component.html',
  styleUrl: './submit-assignment.component.css'
})
export class SubmitAssignmentComponent {

  isDisabled: boolean = false;
  classId: any
  assignments: any
  assignmentIndex: any = '';
  studentAssignmentIndex: any = '';
  grade: any = ''


  constructor(private assignmentService: AssignmentService, private toastService: ToastService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.classId = +params['classId'];
    });

    this.assignmentService.getAssignmentsForClass(this.classId).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.assignments = res;
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

  onSubmit() {
    if (!Number.isInteger(this.grade)) {
      this.toastService.show(
        "الرجاء إدخال درجة صحيحة بدون فواصل عشرية",
        'error'
      );
      return;
    }

    const assignmentId = this.assignments[this.assignmentIndex].id

    const { studentId, id } = this.assignments[this.assignmentIndex].studentsAssignments[this.studentAssignmentIndex]

    const body = {
      id,
      grade: this.grade,
      assignment: {
        id: assignmentId
      },
      student: {
        id: studentId
      },
    }

    this.assignmentService.submitAssignment(body).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.assignments[this.assignmentIndex].studentsAssignments.splice(this.studentAssignmentIndex, 1);
          this.grade = ''
          this.studentAssignmentIndex = ''
          this.toastService.show(res.message, 'success')
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
