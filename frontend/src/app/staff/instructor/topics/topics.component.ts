import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MainTopicService } from '../../../services/topics/main-topic.service';
import { environment } from '../../../../environments/environment';
import { ToastService } from '../../../services/toast.service';
import { SubTopicService } from '../../../services/topics/sub-topic.service';
import { CreateComponent } from './create/create.component';
import { EditComponent } from './edit/edit.component';

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [CommonModule, CreateComponent, EditComponent],
  templateUrl: './topics.component.html',
  styleUrl: './topics.component.css'
})
export class TopicsComponent {
  topics: any;

  @Input() isSubTopic: boolean = false
  @Input() showAddTopic: boolean = false
  @Input() showEditTopic: boolean = false
  @Input() mainTopicId: any;
  @Input() subTopicId: any;
  @Input() topicName: any;

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

  toggleAddTopicOpen(isSubTopic: boolean, mainTopicId?: number) {
    this.mainTopicId = mainTopicId ? mainTopicId : null;
    this.isSubTopic = isSubTopic;
    this.toggleAddTopic();
  }

  toggleAddTopic() {
    this.showAddTopic = !this.showAddTopic;
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
        } else if (error.error.status === "ALGD-409") {
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
        } else if (error.error.status === "ALGD-409") {
          this.toastService.show(error.error.message, 'error');
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }
      }
    })

  }

  handleTopicAdded(topics: any) {
    this.topics = topics
    this.showAddTopic = false;
  }

  toggleEditTopicOpen(isSubTopic: boolean, topicName: string, mainTopicId?: number, subTopicId?: number,) {
    console.log(mainTopicId)
    this.topicName = topicName;
    this.mainTopicId = mainTopicId ? mainTopicId : null;
    this.subTopicId = subTopicId ? subTopicId : null;
    this.isSubTopic = isSubTopic;
    this.toggleEditTopic()
  }

  toggleEditTopic() {
    this.showEditTopic = !this.showEditTopic;

  }

  handleTopicEdited(topics: any) {
    this.topics = topics
    console.log(topics)
    this.showAddTopic = false;
  }

  trackByTopicId(index: number, topic: any): number {
    return topic.id;
  }

  trackBySubTopicId(index: number, subTopic: any): number {
    return subTopic.id;
  }

}
