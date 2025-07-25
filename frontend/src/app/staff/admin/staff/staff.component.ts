import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StaffService } from '../../../services/staff.service';
import { environment } from '../../../../environments/environment';
import { ToastService } from '../../../services/toast.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ConfirmDeleteComponent } from './confirm-delete/confirm-delete.component';

@Component({
  selector: 'app-staff',
  standalone: true,
  imports: [CommonModule, RouterModule, ConfirmDeleteComponent],
  templateUrl: './staff.component.html',
  styleUrl: './staff.component.css'
})
export class StaffComponent {

  @Input() staffId!: number;
  @Input() pageNumber: number = 0;
  @Input() page: any = {};
  staff: any;
  confirmDeleteDisplay: boolean = false;


  @Output() closeClicked: EventEmitter<void> = new EventEmitter<void>();


  constructor(private staffService: StaffService, private toastService: ToastService) { }


  ngOnInit() {
    this.getStaff(this.pageNumber);
  }

  getRoleLabel(permission: any): string {
    switch (permission) {
      case 'ADMIN': return 'مدير';
      case 'SUPERVISOR': return 'مشرف';
      case 'TEACHER': return 'معلم';
      default: return 'غير معروف';
    }
  }


  getStaff(pageNumber: number) {
    this.staffService.getStaff(pageNumber).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }
        if (res) {
          this.page = res;
          this.staff = res.content
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

    this.getStaff(newPage);

    // Here you should load data for the new page, e.g.:
    // this.loadPageData(newPage);

    console.log(`Navigated to page ${newPage + 1}`);
  }

  toggleConfirmDeleteStaff(staffId: number): void {
    this.staffId = staffId;
    this.pageNumber = this.page.number;

    if (this.confirmDeleteDisplay) {
      document.body.classList.remove('body-no-scroll');
    } else {
      document.body.classList.add('body-no-scroll');
    }

    this.confirmDeleteDisplay = !this.confirmDeleteDisplay;
  }


  showToast = (message: string, status: 'success' | 'error') => {
    this.toastService.show(message, status);
  };

  handlePageChange(page: any) {
    this.page = page ?? {}
    this.staff = page.content
  }


}
