import { Component, HostListener } from '@angular/core';
import { SemesterService } from '../../services/semester/semester.service';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../services/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxGaugeModule } from 'ngx-gauge';
import { StudentService } from '../../services/user/student.service';
import { JsonParsePipe } from '../../pipes/json-parse.pipe';

@Component({
  selector: 'app-student',
  imports: [CommonModule, FormsModule, NgxGaugeModule, JsonParsePipe],
  templateUrl: './student.component.html',
  styleUrl: './student.component.css'
})
export class StudentComponent {

  displayedEnrollment: boolean = false;
  grade: string = '';
  isDisabled: boolean = false;
  gaugeSize: number = 250; // default size

  studentPageDetails: any

  gaugeLabel = 'نسبة حضور الفصل';
  gaugeAppendText = '%';

  constructor(private semesterService: SemesterService, private toastService: ToastService, private studentService: StudentService) { }

  ngOnInit() {
    this.updateGaugeSize();
    this.getStudentPageDetails();
  }

  enrollToSemester() {
    if (!this.grade.trim()) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;
    this.semesterService.enrollStudent(this.grade).subscribe({
      next: async (res) => {
        this.displayedEnrollment = false;
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }

        this.getStudentPageDetails();
        this.toastService.show('تم التسجيل في الفصل الدراسي بنجاح', 'success');

        this.toastService.clear()
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
        this.isDisabled = false;
      }
    })


  }

  getStudentPageDetails() {
    this.studentService.getStudentPageDetails().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.displayedEnrollment = !res.enrolled;
          this.studentPageDetails = res
        } else if (!res) {
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


  // Function to convert Western digits to Arabic-Indic digits
  getArabicNumber(num: number): string {
    const arabicDigits = '٠١٢٣٤٥٦٧٨٩';
    return num?.toString().replace(/\d/g, d => arabicDigits[+d]);
  }
  // Use a getter for the gauge label with number
  get attendanceLabel(): string {
    return `${this.getArabicNumber(this.studentPageDetails?.attendancePercentage)}%`;
  }

  get attendanceColor(): string {
    if (this.studentPageDetails?.attendancePercentage < 50) return '#e74c3c';   // red
    if (this.studentPageDetails?.attendancePercentage < 75) return '#f39c12';   // amber
    return '#1abc9c';                                  // green
  }

  announcementTypes = [
    { value: 'INFO', label: 'معلومة' },
    { value: 'ALERT', label: 'تنبيه' },
    { value: 'EVENT', label: 'فعالية' },
    { value: 'REMINDER', label: 'تذكير' }
  ];

  getAnnouncementTypeLabel(type: string): string {
    return this.announcementTypes.find(t => t.value === type)?.label || type;
  }

  getAnnouncementGradient(type: string): string {
    switch (type) {
      case 'INFO': return 'linear-gradient(135deg, #009ffd, #2a2a72)';
      case 'ALERT': return 'linear-gradient(135deg, #dc3545, #e4606d)';
      case 'EVENT': return 'linear-gradient(135deg, #ffc107, #ff8c00)';
      case 'REMINDER': return 'linear-gradient(135deg, #28a745, #20c997)';
      default: return 'linear-gradient(135deg, #6c757d, #343a40)';
    }
  }

  isActiveAnnouncement(ann: any): boolean {
    return new Date(ann.endDateTime) > new Date();
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

  @HostListener('window:resize')
  onResize() {
    this.updateGaugeSize();
  }

  updateGaugeSize() {
    const w = window.innerWidth;
    if (w < 576) this.gaugeSize = 180; // mobile
    else if (w < 768) this.gaugeSize = 220; // tablet
    else if (w < 992) this.gaugeSize = 250; // small laptop
    else this.gaugeSize = 300; // large desktop
  }

}
