import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SalahService } from '../../../../services/salah.service';
import { ToastService } from '../../../../services/toast.service';
import { environment } from '../../../../../environments/environment';
import { QuestionDetailsComponent } from './question-details/question-details.component';
import { AddQuestionComponent } from './add-question/add-question.component';

@Component({
  selector: 'app-questions',
  imports: [RouterModule, CommonModule, QuestionDetailsComponent, AddQuestionComponent],
  templateUrl: './questions.component.html',
  styleUrl: './questions.component.css'
})
export class QuestionsComponent {

  levels = [
    { name: 'الاول', value: 'ONE' },
    { name: 'الثاني', value: 'TWO' },
    { name: 'الثالث', value: 'THREE' },
    { name: 'الرابع', value: 'FOUR' }
  ];

  selectedLevel: string = 'الاول';
  level: string = 'ONE'

  questions: any[] = [];

  @Input() subjects: any[] = [];
  @Input() questionObject: any
  showQuestionDetails: boolean = false;
  showAddQuestion: boolean = false;



  constructor(private salahService: SalahService, private toastService: ToastService) { }


  ngOnInit() {
    this.getQuestions('ONE');
    this.salahService.getSubjects().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          console.log('a')
          this.subjects = res
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

  getQuestions(selectedLevel: string) {
    this.salahService.getQuestions(selectedLevel).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.questions = res;
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

  selectLevel(level: any) {
    this.selectedLevel = level.name;
    this.level = level.value
    this.getQuestions(level.value)
  }

  toggleQuestionDetailsClick(i: number) {

    this.questionObject = this.questions[i]
    this.toggleQuestionDetails()
  }

  toggleAddQuestion() {
    this.showAddQuestion = !this.showAddQuestion;
  }
  toggleQuestionDetails() {
    this.showQuestionDetails = !this.showQuestionDetails;
  }

  refreshList(questions: any) {
    this.questions = questions;
  }
}
