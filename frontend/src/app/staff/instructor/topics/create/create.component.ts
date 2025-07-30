import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { SubTopicService } from '../../../../services/topics/sub-topic.service';
import { environment } from '../../../../../environments/environment';
import { Toast, ToastService } from '../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastComponent } from '../../../../components/toast/toast.component';

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  providers: [ToastService],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() pushTopic = new EventEmitter<{ topicObject: any, isSubTopic: boolean, mainTopicId: any }>();
  @Input() isSubTopic: any;
  @Input() mainTopicId: any;

  name: string = '';
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

  createTopic(name: string) {
    if (!name.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;

    if (this.isSubTopic) {
      this.subTopicService.createSubTopic({ name, mainTopic: { id: Number(this.mainTopicId) } }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.pushTopic.emit({ topicObject: res, isSubTopic: true, mainTopicId: this.mainTopicId });
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
      this.mainTopicService.createMainTopic({ name }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.pushTopic.emit({ topicObject: res, isSubTopic: false, mainTopicId: this.mainTopicId });
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
