import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { SubTopicService } from '../../../../services/topics/sub-topic.service';
import { environment } from '../../../../../environments/environment';
import { Toast, ToastService } from '../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent } from '../../../../components/toast/toast.component';

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  providers: [ToastService],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.css'
})
export class EditComponent {
  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() editTopicEmit = new EventEmitter<object>();
  @Input() isSubTopic: any;
  @Input() mainTopicId: any;
  @Input() subTopicId: any
  @Input() topicName: any;
  @Input() topicIndex: any;

  isDisabled: boolean = false;

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';



  constructor(private mainTopicService: MainTopicService, private subTopicService: SubTopicService, private toastService: ToastService) { }

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

  editTopic(name: string) {
    if (!name.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;

    if (this.isSubTopic) {
      this.subTopicService.editSubTopic({ id: this.subTopicId, name, mainTopic: { id: Number(this.mainTopicId) } }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.editTopicEmit.emit(res);
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
      this.mainTopicService.editMainTopic({ id: this.mainTopicId, name }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.editTopicEmit.emit(res);
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
