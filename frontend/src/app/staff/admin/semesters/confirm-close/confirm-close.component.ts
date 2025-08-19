import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SemesterService } from '../../../../services/semester/semester.service';
import { environment } from '../../../../../environments/environment';

@Component({
    selector: 'app-confirm-close',
    imports: [],
    templateUrl: './confirm-close.component.html',
    styleUrl: './confirm-close.component.css'
})
export class ConfirmCloseComponent {

  @Output() closeClickedClose: EventEmitter<void> = new EventEmitter<void>();
  @Input() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Input() semesterId!: number;
  @Input() pageNumber!: any;
  @Input() showToast!: (message: string, status: 'success' | 'error') => void;
  @Output() page: EventEmitter<string> = new EventEmitter<string>();

  isDisabled: boolean = false;

  constructor(private semesterService: SemesterService) { }

  emitCloseClicked(): void {
    this.closeClickedClose.emit();
  }



  closeSemester() {
    this.isDisabled = true;
    let body = {
      page: this.pageNumber,
      size: environment.pageSize
    }
    this.semesterService.closeSemester(body).subscribe({
      next: (response) => {
        this.isDisabled = false;
        this.page.emit(response);
        this.showToast('تم إغلاق الفصل الدراسي بنجاح', 'success');
        this.closeClicked.emit();
        this.closeClickedClose.emit();
      },
      error: (error) => {
        this.isDisabled = false
        if (!environment.production) {
          console.log(error)
        }

        if (error.error.status === "ALGD-400") {
          this.showToast(error.error.message, 'error');
        } else if (error.error.status === "ALGD-403") {
          this.showToast(error.error.message, 'error');
        } else if (error.error.status === "ALGD-500") {
          this.showToast(error.error.message, 'error');
        } else {
          this.showToast("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
        this.closeClicked.emit();
        this.closeClickedClose.emit();
      }
    });
  }
}
