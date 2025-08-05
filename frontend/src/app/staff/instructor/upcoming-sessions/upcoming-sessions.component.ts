import { Component } from '@angular/core';
import { SessionService } from '../../../services/semester/session.service';
import { ToastService } from '../../../services/toast.service';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upcoming-sessions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upcoming-sessions.component.html',
  styleUrl: './upcoming-sessions.component.css'
})
export class UpcomingSessionsComponent {

  pageNumber: number = 0;

  upcomingSessions: any;
  page: any = {};
  visiblePages: number[] = [];


  constructor(private sessionService: SessionService, private toastService: ToastService) { }


  ngOnInit() {
    this.getUpcomingClasses(this.pageNumber);
  }

  getUpcomingClasses(pageNumber: number) {
    this.sessionService.getUpcomingSessions(pageNumber).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.page = res;
          this.upcomingSessions = res.content
          this.updateVisiblePages();

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

  goToPage(newPage: number) {
    if (newPage < 0 || newPage >= this.page.totalPages) {
      return;
    }

    this.page.number = newPage;
    this.page.first = newPage === 0;
    this.page.last = newPage === this.page.totalPages - 1;

    this.getUpcomingClasses(newPage);

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





}
