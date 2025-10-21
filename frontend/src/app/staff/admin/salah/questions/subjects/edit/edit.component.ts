import { Component, EventEmitter, Input, Output } from '@angular/core';
import { environment } from '../../../../../../../environments/environment';
import { Toast, ToastService } from '../../../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent } from '../../../../../../components/toast/toast.component';
import { SalahService } from '../../../../../../services/salah.service';

@Component({
  selector: 'app-edit',
  imports: [CommonModule, FormsModule, ToastComponent],
  providers: [ToastService],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.css'
})
export class EditComponent {
  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() editSubjectEmit = new EventEmitter<object>();
  @Input() isArea: any;
  @Input() subjectId: any;
  @Input() areaId: any
  @Input() subjectName: any;
  @Input() subjectIndex: any;

  isDisabled: boolean = false;

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';



  constructor(private toastService: ToastService, private salahService: SalahService) { }

  ngOnInit() {
    this.toastService.toastState$.subscribe((toast: Toast | null) => {
      if (toast) {
        this.toastMessage = toast.message;
        this.toastType = toast.type;
      } else {
        this.toastMessage = '';
      }
    });
  }

  editSubject(name: string) {
    if (!name.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;

    if (this.isArea) {
      this.salahService.editArea({ id: this.areaId, name, subject: { id: Number(this.subjectId) } }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.editSubjectEmit.emit(res);
            this.emitCloseClicked()
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
    } else {
      this.salahService.editSubject({ id: this.subjectId, name }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.editSubjectEmit.emit(res);
            this.emitCloseClicked()
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

  emitCloseClicked(): void {
    this.closeClicked.emit();
  }

}
