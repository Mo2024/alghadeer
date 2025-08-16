import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-assignment-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './assignment-details.component.html',
  styleUrl: './assignment-details.component.css'
})
export class AssignmentDetailsComponent {

  @Input() assignmentObject: any
  @Output() closeAssignmentDetails: EventEmitter<void> = new EventEmitter<void>();

  ngOnInit() {
    console.log(this.assignmentObject)
  }

  emitCloseClicked(): void {
    this.closeAssignmentDetails.emit();
  }

  arabicDays: any = ['الأحد', 'الاثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];

  arabicMonths: any = [
    'يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو',
    'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'
  ];
  toArabicNumbers(str: string | number): string {
    const arabicNumbers = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return String(str).replace(/\d/g, d => arabicNumbers[+d]);
  }

  formatArabicDateTime(dateTimeStr: string): string {
    if (!dateTimeStr) return '—';

    const date = new Date(dateTimeStr);

    const day = this.toArabicNumbers(date.getDate());
    const month = this.arabicMonths[date.getMonth()];
    const year = this.toArabicNumbers(date.getFullYear());

    let hours = date.getHours();
    const minutes = date.getMinutes();

    let period = hours < 12 ? 'صباحاً' : 'مساءً';
    if (hours === 0) hours = 12;
    else if (hours > 12) hours -= 12;

    const arabicHours = this.toArabicNumbers(hours);
    const arabicMinutes = minutes > 0 ? ':' + this.toArabicNumbers(minutes.toString().padStart(2, '0')) : '';

    const dayName = this.arabicDays[date.getDay()];

    return `${dayName} ${day} ${month} ${year}<br>${arabicHours}${arabicMinutes} ${period}`;
  }



}
