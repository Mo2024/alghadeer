import { Component, EventEmitter, Output } from '@angular/core';
import { ClassService } from '../../../services/semester/class.service';
import { ToastService } from '../../../services/toast.service';
import { environment } from '../../../../environments/environment';
import { CommonModule, formatDate } from '@angular/common';
import { SessionDetailsComponent } from '../upcoming-sessions/session-details/session-details.component';
import { MainTopicService } from '../../../services/topics/main-topic.service';
import { AssignmentDetailsComponent } from './assignments/assignment-details/assignment-details.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-assigned-classes',
  standalone: true,
  imports: [CommonModule, SessionDetailsComponent, AssignmentDetailsComponent, RouterModule],
  templateUrl: './assigned-classes.component.html',
  styleUrl: './assigned-classes.component.css'
})
export class AssignedClassesComponent {

  assignedClasses: any;

  assignmentObjectInput: any;
  sessionObjectInput: any;
  showSessionDetails: boolean = false
  showAssignmentDetails: boolean = false
  topics: any

  constructor(private classService: ClassService, private toastService: ToastService) { }


  ngOnInit() {
    this.getAssignedClasses();
  }

  getAssignedClasses() {
    this.classService.getAssignedClasses().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.assignedClasses = res;

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

  toArabicNumeral(num: number): string {
    const easternArabicNumerals = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return num
      .toString()
      .split('')
      .map(digit => easternArabicNumerals[+digit])
      .join('');
  }

  formatArabicDateWithDigits(date: Date | string): string {
    const arabicDate = formatDate(date, 'fullDate', 'ar');
    return arabicDate.replace(/\d/g, d => '٠١٢٣٤٥٦٧٨٩'[+d]);
  }

  getArabicStartTimeIfDayMatches(obj: any): string | null {
    const arabicNumbers: Record<string, string> = {
      "0": "٠",
      "1": "١",
      "2": "٢",
      "3": "٣",
      "4": "٤",
      "5": "٥",
      "6": "٦",
      "7": "٧",
      "8": "٨",
      "9": "٩",
      ":": ":"
    };

    const toArabic = (str: string): string =>
      str.split('').map(char => arabicNumbers[char] || char).join('');

    const date = new Date(obj.date);
    const dayOfWeekNames = [
      "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY",
      "THURSDAY", "FRIDAY", "SATURDAY"
    ];
    const dayName = dayOfWeekNames[date.getDay()];

    for (const schedule of obj.class.classSchedules) {
      if (schedule.dayOfWeek === dayName) {
        // Extract hour and minutes from startTime "HH:mm:ss"
        const [hourStr, minuteStr] = schedule.startTime.split(':');

        // Convert hour to number and remove leading zero
        let hour = parseInt(hourStr, 10);
        const minute = parseInt(minuteStr, 10);

        // Determine AM/PM in Arabic
        const isPM = hour >= 12;
        const meridiem = isPM ? 'مساءً' : 'صباحًا';

        // Convert hour to 12-hour format
        hour = hour % 12;
        if (hour === 0) hour = 12;

        // Format time string without seconds
        const timeStr = `${hour}:${minute.toString().padStart(2, '0')}`;

        // Convert to Arabic numbers
        const arabicTime = toArabic(timeStr);

        return `${arabicTime} ${meridiem}`;
      }
    }

    return null;
  }

  arabicDayName(day: string): string {
    const map: { [key: string]: string } = {
      SUNDAY: 'الأحد',
      MONDAY: 'الاثنين',
      TUESDAY: 'الثلاثاء',
      WEDNESDAY: 'الأربعاء',
      THURSDAY: 'الخميس',
      FRIDAY: 'الجمعة',
      SATURDAY: 'السبت',
    };
    return map[day.toUpperCase()] || day;
  }


  formatTime(timeStr: string): string {
    return timeStr.slice(0, 5);
  }

  convertToArabicNumbers(str: string): string {
    const numbersMap: { [key: string]: string } = {
      '0': '٠',
      '1': '١',
      '2': '٢',
      '3': '٣',
      '4': '٤',
      '5': '٥',
      '6': '٦',
      '7': '٧',
      '8': '٨',
      '9': '٩',
    };

    return str.replace(/\d/g, (digit) => numbersMap[digit]);
  }


  convertToArabicTimeWithPeriod(time24: string): string {
    const [hourStr, minuteStr] = time24.split(':');
    let hour = parseInt(hourStr, 10);
    const minute = minuteStr;

    const period = hour >= 12 ? 'مساءً' : 'صباحاً';

    if (hour === 0) {
      hour = 12;
    } else if (hour > 12) {
      hour -= 12;
    }

    const hourArabic = this.convertToArabicNumbers(hour.toString());
    const minuteArabic = this.convertToArabicNumbers(minute);

    return `${hourArabic}:${minuteArabic} ${period}`;
  }

  toggleSessionDetailsClick(classIndex: number, sessionIndex: number) {
    this.sessionObjectInput = { ...this.assignedClasses[classIndex].sessions[sessionIndex] }
    this.sessionObjectInput.class = { ...this.assignedClasses[classIndex] }
    this.sessionObjectInput.class.sessions = null
    console.log(this.sessionObjectInput)
    this.toggleSessionDetails()
  }

  toggleAssignmentDetailsClick(classIndex: number, assignmentIndex: number) {
    this.assignmentObjectInput = { ...this.assignedClasses[classIndex].assignments[assignmentIndex] }
    this.assignmentObjectInput.class = { ...this.assignedClasses[classIndex] }
    this.assignmentObjectInput.class.assignments = null
    this.toggleAssignmentDetails()
  }

  toggleAssignmentDetails() {
    this.showAssignmentDetails = !this.showAssignmentDetails;
  }

  toggleSessionDetails() {
    this.showSessionDetails = !this.showSessionDetails;
  }

  refreshSessionList() {
    this.getAssignedClasses()
  }
  checkIfSessionFinished(session: any, classSchedules: any[]) {
    const daysOfWeek = [
      "SUNDAY",
      "MONDAY",
      "TUESDAY",
      "WEDNESDAY",
      "THURSDAY",
      "FRIDAY",
      "SATURDAY"
    ];

    const sessionDate = new Date(session.date);
    const sessionDay = daysOfWeek[sessionDate.getDay()];

    const matchedSchedule = classSchedules.find(
      (schedule) => schedule.dayOfWeek === sessionDay
    );

    if (!matchedSchedule) {
      return false;
    }

    const [endHour, endMinute, endSecond] = matchedSchedule.endTime.split(":").map(Number);
    const sessionEndDateTime = new Date(sessionDate);
    sessionEndDateTime.setHours(endHour, endMinute, endSecond, 0);

    const now = new Date();

    return now > sessionEndDateTime;
  }

  trackByAssignedClass(index: number, assignedClass: any) {
    return assignedClass.id;
  }

  trackBySchedule(index: number, schedule: any) {
    return schedule.id;
  }

  trackBySession(index: number, session: any) {
    return session.id;
  }
  trackByAssignment(index: number, assignment: any) {
    return assignment.id;
  }



}
