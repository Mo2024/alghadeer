import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { environment } from '../../../../../environments/environment';

import { Toast, ToastService } from '../../../../services/toast.service';

import { ToastComponent } from '../../../../components/toast/toast.component';

import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { SubTopicService } from '../../../../services/topics/sub-topic.service';
import { TopicsGroupsService } from '../../../../services/topics/topics-groups.service';

@Component({
  selector: 'app-create',
  imports: [
    CommonModule,
    FormsModule,
    ToastComponent
  ],
  providers: [ToastService],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {

  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() pushTopic = new EventEmitter<object>();

  @Input() isSubTopic: boolean = false;
  @Input() isMainTopic: boolean = false;

  @Input() mainTopicId: any;
  @Input() topicGroupId: any;

  name: string = '';

  isDisabled: boolean = false;

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private mainTopicService: MainTopicService,
    private subTopicService: SubTopicService,
    private topicsGroupsService: TopicsGroupsService,
    private toastService: ToastService
  ) { }

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

    /*
    =====================================
    CREATE TOPIC GROUP
    =====================================
    */

    if (!this.isSubTopic && !this.isMainTopic) {

      this.topicsGroupsService.createTopicGroup({ name }).subscribe({

        next: (res) => {

          if (!environment.production) {
            console.log(res);
          }

          if (res) {

            this.pushTopic.emit(res);

            this.name = '';


          } else {

            this.toastService.show(
              "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني",
              'error'
            );

          }

        },

        error: (error) => {

          if (!environment.production) {
            console.log(error);
          }

          this.handleError(error);

        }

      });

    }

    /*
    =====================================
    CREATE MAIN TOPIC
    =====================================
    */

    else if (!this.isSubTopic && this.isMainTopic) {

      this.mainTopicService.createMainTopic({
        name,
        topicGroup: {
          id: Number(this.topicGroupId)
        }
      }).subscribe({

        next: (res) => {

          if (!environment.production) {
            console.log(res);
          }

          if (res) {

            this.pushTopic.emit(res);

            this.name = '';


          } else {

            this.toastService.show(
              "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني",
              'error'
            );

          }

        },

        error: (error) => {

          if (!environment.production) {
            console.log(error);
          }

          this.handleError(error);

        }

      });

    }

    /*
    =====================================
    CREATE SUB TOPIC
    =====================================
    */

    else {

      this.subTopicService.createSubTopic({
        name,
        mainTopic: {
          id: Number(this.mainTopicId)
        }
      }).subscribe({

        next: (res) => {

          if (!environment.production) {
            console.log(res);
          }

          if (res) {

            this.pushTopic.emit(res);

            this.name = '';


          } else {

            this.toastService.show(
              "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني",
              'error'
            );

          }

        },

        error: (error) => {

          if (!environment.production) {
            console.log(error);
          }

          this.handleError(error);

        }

      });

    }

  }

  handleError(error: any) {

    if (error.error.status === "ALGD-400") {

      this.toastService.show(error.error.message, 'error');

    } else if (error.error.status === "ALGD-403") {

      this.toastService.show(error.error.message, 'error');

    } else if (error.error.status === "ALGD-409") {

      this.toastService.show(error.error.message, 'error');

    } else if (error.error.status === "ALGD-500") {

      this.toastService.show(error.error.message, 'error');

    } else {

      this.toastService.show(
        "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني",
        'error'
      );

    }

    this.isDisabled = false;

  }

  emitCloseClicked(): void {
    this.closeClicked.emit();
  }

}