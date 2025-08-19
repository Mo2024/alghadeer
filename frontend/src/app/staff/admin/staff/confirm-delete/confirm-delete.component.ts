import { Component, EventEmitter, Input, Output } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { StaffService } from '../../../../services/user/staff.service';

@Component({
    selector: 'app-confirm-delete',
    imports: [],
    templateUrl: './confirm-delete.component.html',
    styleUrl: './confirm-delete.component.css'
})
export class ConfirmDeleteComponent {



  @Output() closeClickedDelete: EventEmitter<void> = new EventEmitter<void>();
  @Input() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Input() staffId!: number;
  @Input() pageNumber!: any;
  @Input() showToast!: (message: string, status: 'success' | 'error') => void;
  @Output() page: EventEmitter<string> = new EventEmitter<string>();


  isDisabled: boolean = false;

  constructor(private staffService: StaffService) { }

  emitCloseClicked(): void {
    this.closeClickedDelete.emit();
  }



  deleteStaff() {
    this.isDisabled = true;
    let body = {
      staff: { id: this.staffId },
      page: this.pageNumber,
      size: environment.pageSize
    }
    if (!environment.production) {
      console.log(body)
    }
    this.staffService.archive(body).subscribe({
      next: (response) => {
        this.isDisabled = false;
        this.page.emit(response);
        this.showToast('تم أرشفة الطاقم بنجاح', 'success');
        this.closeClicked.emit();
        this.closeClickedDelete.emit();
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
        this.closeClickedDelete.emit();
      }
    });
  }

}
