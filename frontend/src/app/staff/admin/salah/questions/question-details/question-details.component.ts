import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../../../environments/environment';
import { ToastService } from '../../../../../services/toast.service';
import { SalahService } from '../../../../../services/salah.service';

@Component({
  selector: 'app-question-details',
  imports: [CommonModule, FormsModule],
  templateUrl: './question-details.component.html',
  styleUrl: './question-details.component.css'
})
export class QuestionDetailsComponent {

  constructor(private salahService: SalahService, private toastService: ToastService) { }

  questionCopy: any

  selectedSubjectIndex: any = ''
  selectedAreaIndex: any = ''

  @Input() level: any
  @Input() questionObject: any
  @Input() subjects: any
  @Output() closeQuestionDetails: EventEmitter<void> = new EventEmitter<void>();
  @Output() refreshList: EventEmitter<void> = new EventEmitter<void>();
  isEditable = false;
  isDisabled = false;


  ngOnInit() {
    for (let i = 0; i < this.subjects.length; i++) {
      if (this.questionObject.subject.id === this.subjects[i].id) {
        this.selectedSubjectIndex = i;
        break;
      }
    }

    for (let i = 0; i < this.subjects[this.selectedSubjectIndex].subjectAreas.length; i++) {

      if (this.questionObject.area.id === this.subjects[this.selectedSubjectIndex].subjectAreas[i].id) {
        this.selectedAreaIndex = i;
        break;
      }
    }

    console.log(this.selectedAreaIndex)
    console.log(this.selectedSubjectIndex)
  }

  toggleEnableEdit() {
    this.selectedSubjectIndex = ''
    this.selectedAreaIndex = ''
    this.questionCopy = this.questionObject
    this.isEditable = !this.isEditable
  }

  emitCloseClicked(): void {
    this.closeQuestionDetails.emit();
  }

  getArabicLevel(level: string): string {
    const levelMap: Record<string, string> = {
      ONE: 'الأول',
      TWO: 'الثاني',
      THREE: 'الثالث',
      FOUR: 'الرابع'
    };
    return levelMap[level] || level;
  }

  cancelEditing() {
    this.questionObject = this.questionCopy
    this.isEditable = !this.isEditable
  }

  submitQuestionChange() {
    this.isDisabled = true
    this.salahService.editQuestion(this.questionObject, this.level).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        this.isDisabled = false
        if (res) {
          this.isEditable = !this.isEditable
          this.refreshList.emit(res)
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

  onDeleteQuestion() {
    this.isDisabled = true
    this.salahService.deleteQuestion(this.level, this.questionObject.id).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        this.isDisabled = false
        if (res) {
          this.isEditable = !this.isEditable
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
