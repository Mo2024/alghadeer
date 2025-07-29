import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MainTopicService } from '../../../services/topics/main-topic.service';
import { environment } from '../../../../environments/environment';
import { ToastService } from '../../../services/toast.service';
import { SubTopicService } from '../../../services/topics/sub-topic.service';

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topics.component.html',
  styleUrl: './topics.component.css'
})
export class TopicsComponent {
  topics: any;

  constructor(private mainTopicsService: MainTopicService, private toastService: ToastService, private subTopicService: SubTopicService) { }

  ngOnInit() {

    this.mainTopicsService.getTopics().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.topics = res
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

  openAddTopicModal() {
    alert('فتح نافذة إضافة موضوع رئيسي (غير متصلة بعد)');
  }

  openAddSubTopicModal(topicId: number) {
    alert(`فتح نافذة إضافة موضوع فرعي للموضوع رقم ${topicId}`);
  }

  editSubTopic(subtopic: any) {
    alert(`تعديل الموضوع الفرعي: ${subtopic.name}`);
  }

  deleteSubTopic(subId: number, mainTopicIndex: number, subTopicIndex: number) {


    this.subTopicService.deleteSubTopic({ id: subId }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.topics[mainTopicIndex].subTopics.splice(subTopicIndex, 1);
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

  deleteMainTopic(mainTopicId: number, mainTopicIndex: number) {
    this.mainTopicsService.deleteMainTopic({ id: mainTopicId }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.topics.splice(mainTopicIndex, 1);
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
