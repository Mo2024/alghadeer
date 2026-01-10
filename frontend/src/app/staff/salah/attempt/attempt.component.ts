import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ToastService } from '../../../services/toast.service';
import { SalahService } from '../../../services/salah.service';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-attempt',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './attempt.component.html',
  styleUrl: './attempt.component.css'
})
export class AttemptComponent {

  constructor(
    private route: ActivatedRoute,
    private toastService: ToastService,
    private salahService: SalahService,
    private router: Router) { }


  subjects: any[] = [];
  selectedSubjectsId: any[] = []


  showQuestions: boolean = false;
  questions: any[] = [];
  studentAttempt: any;

  subjectsHashMap: any = new Map();


  isNew?: boolean;
  attemptId?: number;
  studentId?: number;
  semesterId?: number;
  classId?: number;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.semesterId = parseInt(params['semesterId']);
      this.studentId = parseInt(params['studentId']);
      this.attemptId = parseInt(params['attemptId']);
      this.classId = parseInt(params['classId']);
      this.isNew = params['new'] === 'true';
    });

    if (!Number.isInteger(this.studentId) && this.isNew) {
      this.toastService.show("رقم الطالب غير صحيح", 'error'); // "Invalid student number"
      return;
    }

    if (!Number.isInteger(this.attemptId) && !this.isNew) {
      this.toastService.show("رقم الاختبار غير صحيح", 'error'); // "Invalid attempt number"
      return;
    }

    if (!Number.isInteger(this.classId)) {
      this.toastService.show("رقم الصف غير صحيح", 'error'); // "Invalid attempt number"
      return;
    }

    if (!Number.isInteger(this.semesterId)) {
      this.toastService.show("رقم الفصل غير صحيح", 'error'); // "Invalid attempt number"
      return;
    }

    //checks if this is a fresh attempt, if it is then it will display the subjects to choose then create the attempt 
    // otherwise it will get the questions
    if (this.isNew == true) {

      this.salahService.getSubjectsByLevel(this.studentId).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.subjects = [...res];
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

    } else if (this.isNew == false) {

      this.salahService.getAttemptQuestions(this.attemptId).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.questions = res.salahQuestionsRes;
            this.studentAttempt = res.studentAttempt;
            await this.createSubjectsHashmap()
            this.studentAttempt.isPassed = this.studentAttempt.isPassed === true;
            this.showQuestions = true;
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

  createAttempt() {

    if (this.selectedSubjectsId.length <= 0) {
      this.toastService.show('الرجاء اختيار موضوع واحد على الأقل', 'error');
      return
    }

    this.salahService.createAttempt(this.selectedSubjectsId, this.studentId).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.questions = res.salahQuestionsRes;
          this.studentAttempt = res.studentAttempt;
          await this.createSubjectsHashmap()
          this.studentAttempt.isPassed = this.studentAttempt.isPassed === true;
          this.showQuestions = true;
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

  // this hashmap is to be able to map the failed/passed values to the this.studentAttempt variable 
  // without having to do a nested loop, this will be done by making for each subject id stored
  // in the this.studentAttempt.subjects array, it will make a key based on the subject id and store
  // the index of the id in the array and the value
  async createSubjectsHashmap() {
    for (let i = 0; i < this.studentAttempt.subjects.length; i++) {
      let subjectObject = this.studentAttempt.subjects[i]
      let key = subjectObject.subjectId
      let value = i
      this.subjectsHashMap.set(key, value)
    }
  }


  onSubjectToggle(event: any, subj: any) {
    if (event.target.checked) {
      if (!this.selectedSubjectsId.includes(subj.id)) {
        this.selectedSubjectsId.push(subj.id);
      }
    } else {
      this.selectedSubjectsId = this.selectedSubjectsId.filter(id => id !== subj.id);
    }
  }

  submitAttempt() {

    this.salahService.submitAttempt({
      studentAttempt: this.studentAttempt,
      salahQuestionsRes: null,
      salahQuestionsReq: this.questions
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.router.navigate(
            ['/staff/salah'],
            {
              state: {
                studentId: this.studentId,
                attemptReroute: true,
                classId: this.classId,
                semesterId: this.semesterId
              }
            }
          );

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

  saveAttempt() {
    this.salahService.saveAttempt({
      studentAttempt: this.studentAttempt,
      salahQuestionsRes: null,
      salahQuestionsReq: this.questions
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.router.navigate(
            ['/staff/salah'],
            {
              state: {
                studentId: this.studentId,
                attemptReroute: true,
                classId: this.classId,
                semesterId: this.semesterId
              }
            }
          );

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


  trackBySubjects(index: number, subject: any) {
    return subject.id;
  }

  getSubjects() {
    return this.questions
      .filter((q, i, arr) => i === 0 || q.question.subject.name !== arr[i - 1].question.subject.name)
      .map(q => ({
        ...q.question.subject,
        subjectIndexInStudentAttempt: this.subjectsHashMap.get(q.question.subject.id)
      }));
  }

  getQuestions(i: number) {
    let subjectId = this.getSubjects()[i].id
    return this.questions.filter((q, i, arr) => q.question.subject.id == subjectId)
  }

}
