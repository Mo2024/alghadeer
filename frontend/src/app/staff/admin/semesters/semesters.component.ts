import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ToastService } from '../../../services/toast.service';
import { SemesterService } from '../../../services/semester/semester.service';
import { environment } from '../../../../environments/environment';
import { ConfirmCloseComponent } from './confirm-close/confirm-close.component';

@Component({
  selector: 'app-semesters',
  standalone: true,
  imports: [CommonModule, RouterModule, ConfirmCloseComponent],
  templateUrl: './semesters.component.html',
  styleUrl: './semesters.component.css'
})
export class SemestersComponent {

  @Input() semesterId!: number;
  @Input() pageNumber: number = 0;
  @Input() page: any = {};
  semesters: any;
  confirmCloseDisplay: boolean = false;

  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();

  constructor(private semesterService: SemesterService, private toastService: ToastService) { }


  ngOnInit() {
    this.getSemesters(this.pageNumber);
  }

  getSemesters(pageNumber: number) {
    this.semesterService.getSemesters(pageNumber).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.page = res;
          this.semesters = res.content
          console.log(this.page.number)
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
    // Check bounds
    if (newPage < 0 || newPage >= this.page.totalPages) {
      return; // Ignore invalid page numbers
    }

    this.page.number = newPage;
    this.page.first = newPage === 0;
    this.page.last = newPage === this.page.totalPages - 1;

    this.getSemesters(newPage);

    // Here you should load data for the new page, e.g.:
    // this.loadPageData(newPage);

    console.log(`Navigated to page ${newPage + 1}`);
  }

  toggleConfirmCloseSemester(semesterId: number): void {
    this.semesterId = semesterId;
    this.pageNumber = this.page.number;

    if (this.confirmCloseDisplay) {
      document.body.classList.remove('body-no-scroll');
    } else {
      document.body.classList.add('body-no-scroll');
    }

    this.confirmCloseDisplay = !this.confirmCloseDisplay;
  }


  showToast = (message: string, status: 'success' | 'error') => {
    this.toastService.show(message, status);
  };

  handlePageChange(page: any) {
    this.page = page ?? {}
    this.semesters = page.content
  }

  getSemesterLabel(semester: any): string {
    switch (semester) {
      case 'SUMMER': return 'الفصل الصيفي';
      case 'FIRST': return 'الفصل الأول';
      case 'SECOND': return 'الفصل الثاني';
      default: return 'غير معروف';
    }
  }

}
