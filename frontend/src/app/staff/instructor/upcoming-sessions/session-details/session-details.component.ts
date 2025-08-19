import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SessionService } from '../../../../services/semester/session.service';
import { ToastService } from '../../../../services/toast.service';
import { MainTopicService } from '../../../../services/topics/main-topic.service';
import { environment } from '../../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
    selector: 'app-session-details',
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './session-details.component.html',
    styleUrl: './session-details.component.css'
})
export class SessionDetailsComponent {

  @Input() sessionObject: any
  @Output() closeSessionDetails: EventEmitter<void> = new EventEmitter<void>();
  @Output() refreshSessionList: EventEmitter<void> = new EventEmitter<void>();

  isDisabled: boolean = false;
  isDisabledSubTopic: boolean = false

  isEditable = false;
  selectedMainTopicIndex: any = '';
  selectedSubTopicIndex: any = '';

  removedSubTopic: Map<any, Map<any, any>> = new Map();
  innerMap: Map<any, any> = new Map<any, any>();
  mainTopicIdsOfSubTopicsToChange: Map<any, any> = new Map<any, any>();

  topicsToChange: any = [];

  topics: any;

  arabicDaysMap: any = {
    'SUNDAY': 'الأحد',
    'MONDAY': 'الاثنين',
    'TUESDAY': 'الثلاثاء',
    'WEDNESDAY': 'الأربعاء',
    'THURSDAY': 'الخميس',
    'FRIDAY': 'الجمعة',
    'SATURDAY': 'السبت'
  };


  constructor(private sessionService: SessionService, private toastService: ToastService, private router: Router, private mainTopicService: MainTopicService) { }

  ngOnInit() {

    this.getTopics();

  }


  getTopics() {
    const subTopicsId: any = []
    this.sessionObject.subTopics.forEach((subtopic: any) => {
      subTopicsId.push(subtopic.id)

    });
    console.log(subTopicsId)
    this.mainTopicService.getTopics().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.topics = res;
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

  toggleChangeSubTopicBtn() {
    this.isDisabledSubTopic = true
    this.isEditable = true
    this.getTopics();
    for (let i = 0; i < this.sessionObject.subTopics.length; i++) {
      const subTopic = this.sessionObject.subTopics[i]
      const mainTopicId = subTopic.mainTopicId

      let innerMap = this.removedSubTopic.get(mainTopicId);
      if (!innerMap) {
        innerMap = new Map<any, any>();
      }

      innerMap.set(subTopic.id, subTopic);
      this.removedSubTopic.set(mainTopicId, innerMap);

      this.mainTopicIdsOfSubTopicsToChange.set(subTopic.id, mainTopicId);
      this.topicsToChange.push(subTopic.id);

      this.selectedSubTopicIndex = '';
    }
    // console.log(this.mainTopicsId)
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

  onMainTopicChange() {

    // Optional: Reset subtopic when main topic changes
    this.selectedSubTopicIndex = '';
  }

  emitCloseClicked(): void {
    this.closeSessionDetails.emit();
  }

  cancelEditing() {
    this.selectedMainTopicIndex = '';
    this.selectedSubTopicIndex = ''
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

          const newSubTopics: any = []
          this.topicsToChange.forEach((subTopicId: number) => {
            const mainTopicId = this.mainTopicIdsOfSubTopicsToChange.get(subTopicId)
            const subTopic = this.removedSubTopic.get(mainTopicId)?.get(subTopicId)
            newSubTopics.push(subTopic)
          });
          this.sessionObject.subTopics = newSubTopics;

          this.selectedMainTopicIndex = '';
          this.selectedSubTopicIndex = '';

          this.removedSubTopic.clear();
          this.innerMap.clear();
          this.mainTopicIdsOfSubTopicsToChange.clear();
          this.topicsToChange = []
            ;
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

  addSubTopic() {
    if (this.selectedSubTopicIndex === '') return;

    const subTopic = this.topics[this.selectedMainTopicIndex].subTopics[this.selectedSubTopicIndex];
    const mainTopicId = this.topics[this.selectedMainTopicIndex];

    let innerMap = this.removedSubTopic.get(mainTopicId);
    if (!innerMap) {
      innerMap = new Map<any, any>();
    }

    innerMap.set(subTopic.id, subTopic);
    this.removedSubTopic.set(mainTopicId, innerMap);

    this.mainTopicIdsOfSubTopicsToChange.set(subTopic.id, mainTopicId);
    this.topicsToChange.push(subTopic.id);

    this.topics[this.selectedMainTopicIndex].subTopics.splice(this.selectedSubTopicIndex, 1);

    this.selectedSubTopicIndex = '';
  }


  removeSubTopic(subTopicId: number, index: number) {
    this.getSubTopicName(subTopicId)

    const mainTopicId = this.mainTopicIdsOfSubTopicsToChange.get(subTopicId)
    const subtopic = this.removedSubTopic.get(mainTopicId)?.get(subTopicId)

    this.mainTopicIdsOfSubTopicsToChange.delete(subTopicId)
    this.removedSubTopic.get(mainTopicId)?.delete(subTopicId);
    this.topicsToChange.splice(index, 1)
    const mainTopicIndex = this.topics.findIndex(
      (mainTopic: any) => mainTopic.id === mainTopicId
    );
    console.log(mainTopicIndex)

    const isDuplicate = this.topics[mainTopicIndex].subTopics.some(
      (subTopic: any) => subTopic.id === subTopic.id
    );

    if (!isDuplicate) this.topics[mainTopicIndex].subTopics.push(subtopic)

  }

  getSubTopicName(subTopicId: number) {
    const mainTopicId = this.mainTopicIdsOfSubTopicsToChange.get(subTopicId)
    const subtopic = this.removedSubTopic.get(mainTopicId)?.get(subTopicId)
    return subtopic.name
  }
}
