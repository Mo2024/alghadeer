import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SessionService } from '../../../../services/semester/session.service';
import { ToastService } from '../../../../services/toast.service';
import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { environment } from '../../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TopicsGroupsService } from '../../../../services/topics/topics-groups.service';
import { TreeNode } from 'primeng/api';
import { TreeSelectModule } from 'primeng/treeselect';


@Component({
  selector: 'app-session-details',
  imports: [CommonModule, FormsModule, RouterModule, TreeSelectModule],
  templateUrl: './session-details.component.html',
  styleUrl: './session-details.component.css'
})
export class SessionDetailsComponent {

  @Input() sessionObject: any
  @Output() closeSessionDetails: EventEmitter<void> = new EventEmitter<void>();
  @Output() refreshSessionList: EventEmitter<void> = new EventEmitter<void>();

  sessionObjectSubTopicsBkp: any = []

  isDisabled: boolean = false;
  isDisabledSubTopic: boolean = false

  isEditable = false;
  selectedMainTopicIndex: any = '';
  selectedSubTopicIndex: any = '';

  removedSubTopic: Map<any, Map<any, any>> = new Map();
  innerMap: Map<any, any> = new Map<any, any>();
  mainTopicIdsOfSubTopicsToChange: Map<any, any> = new Map<any, any>();
  subTopicsNodeToAdd: Map<any, any> = new Map<any, any>();



  topicsToChange: any = [];

  topics: any;
  topicGroups: any;
  topicTree: TreeNode[] = [];
  selectedTopic: any

  arabicDaysMap: any = {
    'SUNDAY': 'الأحد',
    'MONDAY': 'الاثنين',
    'TUESDAY': 'الثلاثاء',
    'WEDNESDAY': 'الأربعاء',
    'THURSDAY': 'الخميس',
    'FRIDAY': 'الجمعة',
    'SATURDAY': 'السبت'
  };


  constructor(private sessionService: SessionService, private topicsGroupsService: TopicsGroupsService, private toastService: ToastService, private router: Router, private mainTopicService: MainTopicService) { }

  ngOnInit() {
    // this.getTopics();
    // this.loadTopicGroups();
  }

  loadTopicGroups() {
    this.sessionObjectSubTopicsBkp = [...this.sessionObject.subTopics]

    this.topicsGroupsService.getTopicsGroups().subscribe({
      next: (res) => {

        if (!environment.production) {
          console.log(res);
        }

        if (res) {
          this.topicGroups = res;
          this.buildTopicTree();

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

  buildTopicTree(): void {
    const sessionSubTopicIds = this.sessionObject.subTopics.map(
      (subTopic: any) => subTopic.id
    );

    this.topicTree = []
    this.topicsToChange = []

    this.topicTree = this.topicGroups.map((group: any, groupIndex: number) => ({
      key: `group-${group.id}`,
      label: group.name,
      data: group,
      selectable: false,
      children: group.mainTopics.map((mainTopic: any, mainIndex: number) => ({
        key: `main-${mainTopic.id}`,
        label: mainTopic.name,
        data: mainTopic,
        selectable: false,

        children: mainTopic.subTopics
          .map((subTopic: any) => {
            const leafNode = {
              key: `sub-${subTopic.id}`,
              label: subTopic.name,
              data: subTopic,
              leaf: true,
              selectable: true,
              groupIndex,
              mainIndex,
            }
            const isInSession = sessionSubTopicIds.includes(subTopic.id);

            if (isInSession) {
              console.log(subTopic)
              this.loadSubTopicToList(
                leafNode);
            }

            return leafNode

          })
          .filter((subTopic: any) => {
            return !sessionSubTopicIds.includes(subTopic.data.id);
          })
      }))
    }));

    console.log(this.topicTree)
  }

  onTreeNodeSelect(event: any): void {
    const node: any = event.node;

    if (!node.leaf) {
      node.expanded = !node.expanded;
      this.selectedTopic = null;
      return;
    }

    this.addSubTopic(
      node
    );
  }

  toggleChangeSubTopicBtn() {
    this.isDisabledSubTopic = true
    this.isEditable = true
    this.loadTopicGroups();
    this.isDisabledSubTopic = false

  }
  // Arabic digits
  formatArabicDateWithDigits(dateString: string): string {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' } as const;
    const date = new Date(dateString);
    return date.toLocaleDateString('ar-EG', options);
  }

  getArabicDayName(dateString: string): string {
    const day = new Date(dateString).toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
    return this.arabicDaysMap[day] || day;
  }

  getArabicScheduleTimeRange(session: any): string {
    const arabicNumbers: Record<string, string> = {
      "0": "٠", "1": "١", "2": "٢", "3": "٣", "4": "٤",
      "5": "٥", "6": "٦", "7": "٧", "8": "٨", "9": "٩", ":": ":"
    };

    const toArabic = (str: string): string =>
      str.split('').map(char => arabicNumbers[char] || char).join('');

    const date = new Date(session.date);
    const dayOfWeekNames = [
      "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY",
      "THURSDAY", "FRIDAY", "SATURDAY"
    ];
    const dayName = dayOfWeekNames[date.getDay()];

    const schedule = session.class.classSchedules.find(
      (s: any) => s.dayOfWeek === dayName
    );

    if (!schedule) return 'لم يحدد';

    const formatArabicTime = (time: string): string => {
      const [hourStr, minuteStr] = time.split(':');
      let hour = parseInt(hourStr, 10);
      const minute = parseInt(minuteStr, 10);
      const isPM = hour >= 12;
      const meridiem = isPM ? 'مساءً' : 'صباحًا';

      hour = hour % 12;
      if (hour === 0) hour = 12;

      const timeStr = `${hour}:${minute.toString().padStart(2, '0')}`;
      return `${toArabic(timeStr)} ${meridiem}`;
    };

    const start = formatArabicTime(schedule.startTime);
    const end = formatArabicTime(schedule.endTime);

    return `${start} الى ${end}`;
  }

  emitCloseClicked(): void {
    this.closeSessionDetails.emit();
  }

  cancelEditing() {
    this.sessionObject.subTopics = [...this.sessionObjectSubTopicsBkp]
    this.topicsToChange = []
    this.subTopicsNodeToAdd = new Map<any, any>();
    this.isEditable = !this.isEditable
  }

  submitTopicChange() {
    this.isDisabled = true
    this.sessionService.changeSessionSubTopic({
      sessionId: this.sessionObject.id,
      subTopicsId: this.topicsToChange
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        this.isDisabled = false
        if (res) {



          this.buildTopicTree()


          this.isEditable = !this.isEditable
          this.refreshSessionList.emit()
        } else {
          this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
        }

      },
      error: (error) => {
        if (!environment.production) {
          console.log(error)
        }
        this.isDisabled = false

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

  cancelSession() {
    this.sessionService.cancelSessionsBySessionId([this.sessionObject.id]).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.refreshSessionList.emit()
          this.closeSessionDetails.emit()
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

  addSubTopic(subTopicNode: any) {

    this.subTopicsNodeToAdd.set(subTopicNode.data.id, subTopicNode)
    this.topicsToChange.push(subTopicNode.data.id);

    console.log(subTopicNode.data)
    this.sessionObject.subTopics.push(subTopicNode.data)
    console.log(this.sessionObject.subTopics)

    const mainNode =
      this.topicTree[subTopicNode.groupIndex]
        .children?.[subTopicNode.mainIndex];

    if (mainNode) {
      mainNode.children = mainNode.children?.filter(
        (node: any) => node.data.id !== subTopicNode.data.id
      ) ?? [];
    }

    this.selectedTopic = null;
  }

  loadSubTopicToList(subTopicNode: any) {


    this.subTopicsNodeToAdd.set(subTopicNode.data.id, subTopicNode)

    this.topicsToChange.push(subTopicNode.data.id);

  }


  removeSubTopic(subTopicId: number) {

    const subTopicNode = this.subTopicsNodeToAdd.get(subTopicId);

    this.topicTree[subTopicNode.groupIndex].children?.[subTopicNode.mainIndex]?.children?.push(subTopicNode);

    this.subTopicsNodeToAdd.delete(subTopicId);

    this.topicsToChange = this.topicsToChange.filter(
      (id: any) => id !== subTopicId
    );

    this.sessionObject.subTopics = this.sessionObject.subTopics.filter(
      (subTopic: any) => subTopic.id !== subTopicId
    );

  }

}
