import { Component } from '@angular/core';
import { AnnouncementService } from '../../../services/semester/announcement.service';
import { ToastService } from '../../../services/toast.service';
import { environment } from '../../../../environments/environment';
import { CommonModule, formatDate } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-announcements',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './announcements.component.html',
  styleUrl: './announcements.component.css'
})
export class AnnouncementsComponent {

  pageNumber: number = 0;

  announcements: any;
  page: any = {};
  visiblePages: number[] = [];

  announcementTypes = [{ value: 'INFO', label: 'معلومة' }, { value: 'ALERT', label: 'تنبيه' }, { value: 'EVENT', label: 'فعالية' }, { value: 'REMINDER', label: 'تذكير' }];

  constructor(private announcementsService: AnnouncementService, private toastService: ToastService) { }

  ngOnInit() {
    this.getAnnouncements(this.pageNumber)
  }

  getAnnouncementTypeLabel(value: string): string {
    const type = this.announcementTypes.find(t => t.value === value);
    return type ? type.label : value; // fallback to value if not found
  }

  getAnnouncements(pageNumber: number) {
    this.announcementsService.getAnnouncements(pageNumber).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.page = res;
          this.announcements = res.content
          this.updateVisiblePages();

        }

      },
      error: (error) => {

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

  updateVisiblePages() {
    const totalPages = this.page.totalPages;
    const currentPage = this.page.number;

    const pages: number[] = [];

    // Always show first page
    pages.push(0);

    if (currentPage > 1 && currentPage < totalPages - 2) {
      // Current page somewhere in the middle, show ellipsis once with current page
      pages.push(-1);         // Ellipsis placeholder
      pages.push(currentPage);
    } else if (currentPage <= 1) {
      // Near start, show page 1 and 2 explicitly (if exist)
      for (let i = 1; i <= Math.min(2, totalPages - 2); i++) {
        pages.push(i);
      }
    } else {
      // Near end, show last few pages before last explicitly
      for (let i = Math.max(totalPages - 3, 1); i < totalPages - 1; i++) {
        pages.push(i);
      }
    }

    // Always show last page if more than 1 page
    if (totalPages > 1) {
      pages.push(totalPages - 1);
    }

    this.visiblePages = pages;
  }

  toArabicNumbers(str: string | number): string {
    const arabicNumbers = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return String(str).replace(/\d/g, d => arabicNumbers[+d]);
  }

  arabicMonths: any = [
    'يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو',
    'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'
  ];

  arabicDays: any = ['الأحد', 'الاثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];


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

    return `${day} ${month} ${year}، ${arabicHours}${arabicMinutes} ${period}`;
  }


  toArabicNumeral(num: number): string {
    const easternArabicNumerals = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return num
      .toString()
      .split('')
      .map(digit => easternArabicNumerals[+digit])
      .join('');
  }

  goToPage(newPage: number) {
    if (newPage < 0 || newPage >= this.page.totalPages) {
      return;
    }

    this.page.number = newPage;
    this.page.first = newPage === 0;
    this.page.last = newPage === this.page.totalPages - 1;

    this.getAnnouncements(newPage);

  }

  cancelAnnouncement(id: any, index: any) {
    this.announcementsService.cancelAnnouncement(id).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.announcements[index].cancelled = true
          this.page.content[index].cancelled = true

        }

      },
      error: (error) => {

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
