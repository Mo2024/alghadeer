import { Component, EventEmitter, Input, Output } from '@angular/core';
import { environment } from '../../../../../../../environments/environment';
import { Toast, ToastService } from '../../../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent } from '../../../../../../components/toast/toast.component';
import { SalahService } from '../../../../../../services/salah.service';

@Component({
  selector: 'app-create',
  imports: [CommonModule, FormsModule, ToastComponent],
  providers: [ToastService],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() pushSubject = new EventEmitter<object>();
  @Input() isArea: any;
  @Input() subjectId: any;

  name: string = '';
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

  createSubject(name: string) {
    if (!name.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;

    if (this.isArea) {
      this.salahService.createArea({ name, subject: { id: Number(this.subjectId) } }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.pushSubject.emit(res);
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
      this.salahService.createSubject({ name }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.pushSubject.emit(res);
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
