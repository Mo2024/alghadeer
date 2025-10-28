import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SalahService } from '../../../../../services/salah.service';
import { ToastService } from '../../../../../services/toast.service';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-add-question',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-question.component.html',
  styleUrl: './add-question.component.css'
})
export class AddQuestionComponent {

  constructor(private salahService: SalahService, private toastService: ToastService) { }


  question = {
    question: '',
    level: '',
    sequence: '',
    isPillar: false,
    area: {
      id: '',
    },
    subject: {
      id: '',
    }
  }

  @Input() subjects: any
  @Input() level: any
  @Output() refreshList: EventEmitter<void> = new EventEmitter<void>();
  @Output() closeAddQuestion: EventEmitter<void> = new EventEmitter<void>();

  selectedSubjectIndex: any = ''
  selectedAreaIndex: any = ''
  isDisabled: boolean = false;

  submitQuestion() {

    if (
      this.selectedSubjectIndex === '' ||
      this.selectedSubjectIndex === undefined ||
      !this.subjects[this.selectedSubjectIndex] ||
      !this.subjects[this.selectedSubjectIndex].id
    ) {
      this.showError('يرجى اختيار الموضوع قبل المتابعة');
      return;
    }

    if (
      this.selectedAreaIndex === '' ||
      this.selectedAreaIndex === undefined ||
      !this.subjects[this.selectedSubjectIndex].subjectAreas[this.selectedAreaIndex]
    ) {
      this.showError('يرجى اختيار المحور قبل المتابعة');
      return;
    }

    this.question.subject.id = this.subjects[this.selectedSubjectIndex].id
    this.question.area.id = this.subjects[this.selectedSubjectIndex].subjectAreas[this.selectedAreaIndex].id
    console.log(this.question)
    this.isDisabled = true
    this.salahService.createQuestion(this.question, this.level).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        this.isDisabled = false
        if (res) {
          this.refreshList.emit(res)
          this.emitCloseClicked();
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }

      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }
        this.isDisabled = false

        if (error.error.status === "ALGD-400") {
          this.showError(error.error.message);
        } else if (error.error.status === "ALGD-403") {
          this.showError(error.error.message);
        } else if (error.error.status === "ALGD-500") {
          this.showError(error.error.message);
        } else {
          this.showError("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني");
        }
      }
    })
  }


  emitCloseClicked(): void {
    this.closeAddQuestion.emit();
  }

  showToast = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  showSuccess(message: string) {
    this.toastMessage = message;
    this.toastType = 'success';
    this.showToast = true;
  }

  showError(message: string) {
    this.toastMessage = message;
    this.toastType = 'error';
    this.showToast = true;
  }

  closeToast() {
    this.showToast = false;
  }


}
