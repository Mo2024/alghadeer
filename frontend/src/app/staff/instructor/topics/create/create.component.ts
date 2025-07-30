import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { SubTopicService } from '../../../../services/topics/sub-topic.service';
import { environment } from '../../../../../environments/environment';
import { ToastService } from '../../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() pushTopic = new EventEmitter<{ topicObject: any, isSubTopic: boolean }>();
  @Input() mainTopics: any;
  @Input() isSubTopic: any;

  name: string = '';
  mainTopicId: string = '';
  isDisabled: boolean = false;

  constructor(private mainTopicService: MainTopicService, private subTopicService: SubTopicService, private toastService: ToastService) { }

  createTopic(name: string) {
    if (!name.trim() && (!this.mainTopicId.trim() && this.isSubTopic)) {
      // alert('Name and description cannot be empty');
      return; // Exit the function if empty
    }
    this.isDisabled = true;

    if (this.isSubTopic) {
      this.subTopicService.createSubTopic({ name, mainTopic: { id: Number(this.mainTopicId) } }).subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
          }

          if (res) {
            this.pushTopic.emit({ topicObject: res, isSubTopic: true });
          } else {
            // this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
          }

        },
        error: (error) => {
          if (!environment.production) {
            console.log(error)
          }

          if (error.error.status === "ALGD-400") {
            // this.toastService.show(error.error.message, 'error');
          } else if (error.error.status === "ALGD-403") {
            // this.toastService.show(error.error.message, 'error');
          } else if (error.error.status === "ALGD-500") {
            // this.toastService.show(error.error.message, 'error');
          } else {
            // this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
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
            this.pushTopic.emit({ topicObject: res, isSubTopic: false });
          } else {
            // this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
          }

        },
        error: (error) => {
          if (!environment.production) {
            console.log(error)
          }

          if (error.error.status === "ALGD-400") {
            // this.toastService.show(error.error.message, 'error');
          } else if (error.error.status === "ALGD-403") {
            // this.toastService.show(error.error.message, 'error');
          } else if (error.error.status === "ALGD-500") {
            // this.toastService.show(error.error.message, 'error');
          } else {
            // this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
          }
        }
      })
    }

  }

  emitCloseClicked(): void {
    this.closeClicked.emit();
  }
}
