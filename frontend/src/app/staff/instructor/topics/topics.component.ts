import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { environment } from '../../../../environments/environment';

import { ToastService } from '../../../services/toast.service';

import { CreateComponent } from './create/create.component';
import { EditComponent } from './edit/edit.component';

import { MainTopicService } from '../../../services/topics/main-topic.service';
import { SubTopicService } from '../../../services/topics/sub-topic.service';
import { TopicsGroupsService } from '../../../services/topics/topics-groups.service';

@Component({
  selector: 'app-topics',
  imports: [CommonModule, CreateComponent, EditComponent],
  templateUrl: './topics.component.html',
  styleUrl: './topics.component.css'
})
export class TopicsComponent {

  topicGroups: any[] = [];

  /*
  =====================================
  CREATE / EDIT STATES
  =====================================
  */

  @Input() isSubTopic: boolean = false;
  @Input() isMainTopic: boolean = false;

  @Input() showAddTopic: boolean = false;
  @Input() showEditTopic: boolean = false;

  @Input() mainTopicId: any;
  @Input() subTopicId: any;
  @Input() topicGroupId: any;

  @Input() topicName: any;

  constructor(
    private mainTopicsService: MainTopicService,
    private subTopicService: SubTopicService,
    private topicsGroupsService: TopicsGroupsService,
    private toastService: ToastService
  ) { }

  ngOnInit() {
    this.loadTopicGroups();
  }

  /*
  =====================================
  LOAD
  =====================================
  */

  loadTopicGroups() {
    this.topicsGroupsService.getTopicsGroups().subscribe({
      next: (res) => {

        if (!environment.production) {
          console.log(res);
        }

        if (res) {
          this.topicGroups = res;
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

        this.toastService.show(
          "حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني",
          'error'
        );
      }
    });
  }

  /*
  =====================================
  CREATE MODAL
  =====================================
  */

  toggleAddTopicOpen(
    isSubTopic: boolean,
    isMainTopic: boolean,
    topicGroupId?: number,
    mainTopicId?: number
  ) {

    this.isSubTopic = isSubTopic;
    this.isMainTopic = isMainTopic;

    this.topicGroupId = topicGroupId || null;
    this.mainTopicId = mainTopicId || null;

    this.toggleAddTopic();
  }

  toggleAddTopic() {
    this.showAddTopic = !this.showAddTopic;
  }

  /*
  =====================================
  EDIT MODAL
  =====================================
  */

  toggleEditTopicOpen(
    isSubTopic: any,
    isMainTopic: any,
    topicName: any,
    topicGroupId?: any,
    mainTopicId?: any,
    subTopicId?: any
  ) {

    this.isSubTopic = isSubTopic;
    this.isMainTopic = isMainTopic;

    this.topicName = topicName;

    this.topicGroupId = topicGroupId || null;
    this.mainTopicId = mainTopicId || null;
    this.subTopicId = subTopicId || null;

    this.toggleEditTopic();
  }

  toggleEditTopic() {
    this.showEditTopic = !this.showEditTopic;
  }

  /*
  =====================================
  DELETE GROUP
  =====================================
  */

  deleteTopicGroup(groupId: number, groupIndex: number) {

    this.topicsGroupsService.deleteTopicGroup({ id: groupId }).subscribe({

      next: (res) => {

        if (res) {
          this.topicGroups.splice(groupIndex, 1);
        }
      },

      error: (error) => {

        if (!environment.production) {
          console.log(error);
        }

        this.toastService.show(error.error.message, 'error');
      }
    });
  }

  /*
  =====================================
  DELETE MAIN TOPIC
  =====================================
  */

  deleteMainTopic(
    mainTopicId: number,
    groupIndex: number,
    mainTopicIndex: number
  ) {

    this.mainTopicsService.deleteMainTopic({ id: mainTopicId }).subscribe({

      next: (res) => {

        if (res) {
          this.topicGroups[groupIndex]
            .mainTopics
            .splice(mainTopicIndex, 1);
        }
      },

      error: (error) => {

        if (!environment.production) {
          console.log(error);
        }

        this.toastService.show(error.error.message, 'error');
      }
    });
  }

  /*
  =====================================
  DELETE SUB TOPIC
  =====================================
  */

  deleteSubTopic(
    subId: number,
    groupIndex: number,
    mainTopicIndex: number,
    subTopicIndex: number
  ) {

    this.subTopicService.deleteSubTopic({ id: subId }).subscribe({

      next: (res) => {

        if (res) {

          this.topicGroups[groupIndex]
            .mainTopics[mainTopicIndex]
            .subTopics
            .splice(subTopicIndex, 1);
        }
      },

      error: (error) => {

        if (!environment.production) {
          console.log(error);
        }

        this.toastService.show(error.error.message, 'error');
      }
    });
  }

  /*
  =====================================
  HANDLE CREATE / EDIT
  =====================================
  */

  handleTopicAdded(res: any) {
    this.topicGroups = res;
    this.showAddTopic = false;
  }

  handleTopicEdited(res: any) {
    this.topicGroups = res;
    this.showEditTopic = false;
  }

  /*
  =====================================
  TRACK BY
  =====================================
  */

  trackByGroupId(index: number, item: any): number {
    return item.id;
  }

  trackByTopicId(index: number, item: any): number {
    return item.id;
  }

  trackBySubTopicId(index: number, item: any): number {
    return item.id;
  }
}