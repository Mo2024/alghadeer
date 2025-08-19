import { Component } from '@angular/core';
import { ToastService } from '../../../services/toast.service';
import { ClassService } from '../../../services/semester/class.service';
import { StudentService } from '../../../services/user/student.service';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-transfer-student',
    imports: [CommonModule, FormsModule],
    templateUrl: './transfer-student.component.html',
    styleUrl: './transfer-student.component.css'
})
export class TransferStudentComponent {

  studentsOriginalArray: any = []
  addedStudents: any = []
  classId: any = '';
  isDisabled: boolean = false
  classes: any[] = [];
  enrolledStudents: any[] = [];
  removedStudentsName: Map<number, string> = new Map<number, string>();
  selectedStudendIndex: any = ''
  constructor(private toastService: ToastService, private classService: ClassService, private studentService: StudentService) { }

  ngOnInit() {
    this.studentService.getEnrolledStudents().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.enrolledStudents = [...res];
          this.studentsOriginalArray = [...res];

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


  onSubmit() {
    if (!this.classId.trim() || this.addedStudents.length == 0) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    const body = { studentsId: this.addedStudents, classId: this.classId }
    this.classService.changeStudentClass(body).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        this.addedStudents = [];
        this.classId = '';
        this.enrolledStudents = [...this.studentsOriginalArray];
        this.selectedStudendIndex = ''
        this.toastService.show(res.message, 'success');

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

  addstudent(enrolledStudentsIndex: number) {
    let student = this.enrolledStudents[enrolledStudentsIndex].student
    let id = student.id;
    let name = student.name;

    this.removedStudentsName.set(id, name);
    this.enrolledStudents.splice(enrolledStudentsIndex, 1);
    this.addedStudents.push(id);
    this.selectedStudendIndex = ''
  }

  removeStudent(addedStudentsIndex: number) {
    let id = this.addedStudents[addedStudentsIndex];
    let name = this.removedStudentsName.get(id);
    let student = { "student": { id, name } }

    this.enrolledStudents.push(student);
    this.removedStudentsName.delete(id);
    this.addedStudents.splice(addedStudentsIndex, 1);
  }



}
