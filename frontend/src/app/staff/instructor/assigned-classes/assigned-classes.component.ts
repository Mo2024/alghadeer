import { Component } from '@angular/core';
import { ClassService } from '../../../services/semester/class.service';
import { ToastService } from '../../../services/toast.service';
import { environment } from '../../../../environments/environment';
import { CommonModule, formatDate } from '@angular/common';

@Component({
  selector: 'app-assigned-classes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './assigned-classes.component.html',
  styleUrl: './assigned-classes.component.css'
})
export class AssignedClassesComponent {

  assignedClasses: any;


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

}
