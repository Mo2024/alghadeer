import { Component } from '@angular/core';
import { SemesterService } from '../../../../services/semester/semester.service';
import { ToastService } from '../../../../services/toast.service';
import defaultClassesTemplate from '../../../../../assets/defaultClassesTemplate.json'
import customClassesTemplate from '../../../../../assets/customClassesTemplate.json'
import gradesTemplate from '../../../../../assets/gradesTemplate.json'
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../../environments/environment';
import { Router } from '@angular/router';
import { StaffService } from '../../../../services/user/staff.service';
@Component({
  selector: 'app-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {

  defaultClasses: any = defaultClassesTemplate.classes;
  customClasses: any = customClassesTemplate.classes;

  defaultStartTimeInput: any = ['', '', '', '', '', '', '', '', '']
  defaultEndTimeInput: any = ['', '', '', '', '', '', '', '', '']
  defaultDayOfWeekInput: any = ['', '', '', '', '', '', '', '', '']

  customStartTimeInput: any = ['']
  customEndTimeInput: any = ['']
  customDayOfWeekInput: any = ['']

  grades: any = gradesTemplate.grades;
  renderedClasses: any;
  renderedStartTimeInput: any;
  renderedEndTimeInput: any;
  renderedDayOfWeekInput: any;
  isDefaultClasses: boolean = false;
  staffList: any;
  selectedGrade: string = ''


  name: string = '';
  semester: string = '';
  startDate: string = '';
  endDate: string = '';

  isDisabled: boolean = false;

  constructor(private semesterService: SemesterService, private toastService: ToastService, private router: Router, private staffService: StaffService) { }

  ngOnInit() {
    this.renderedClasses = this.isDefaultClasses ? this.defaultClasses : this.customClasses;
    this.renderedDayOfWeekInput = this.isDefaultClasses ? this.defaultDayOfWeekInput : this.customDayOfWeekInput
    this.renderedStartTimeInput = this.isDefaultClasses ? this.defaultStartTimeInput : this.customStartTimeInput
    this.renderedEndTimeInput = this.isDefaultClasses ? this.defaultEndTimeInput : this.customEndTimeInput

    this.staffService.getStaffList().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.staffList = res
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

  createSemester() {

    const body = {
      name: this.name,
      semester: this.semester,
      startDate: this.startDate,
      endDate: this.endDate,
      defaultClasses: this.isDefaultClasses,
      classes: this.renderedClasses
    };

    if (!body.name.trim() || !body.startDate.trim() || !body.endDate.trim() || !body.semester.trim() || body.classes.length < 1) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }

    this.isDisabled = true;
    this.semesterService.createSemester(body).subscribe({
      next: async (res) => {
        this.isDisabled = false;
        if (!environment.production) {
          console.log(res)
        }
        this.router.navigate(['/staff/admin/semesters']);
        this.toastService.show('تم إنشاء الفصل الدراسي بنجاح', 'success');
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

  onDefaultClassesToggle(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;

    this.renderedClasses = checked ? this.defaultClasses : this.customClasses; //In JavaScript/TypeScript, assigning an object or array doesn't copy the data — it copies the reference.

    this.renderedDayOfWeekInput = checked ? this.defaultDayOfWeekInput : this.customDayOfWeekInput
    this.renderedStartTimeInput = checked ? this.defaultStartTimeInput : this.customStartTimeInput
    this.renderedEndTimeInput = checked ? this.defaultEndTimeInput : this.customEndTimeInput

  }



  addClassSchedule(classIndex: number) {
    if (this.renderedDayOfWeekInput[classIndex] == '' && this.renderedStartTimeInput[classIndex] == '' && this.renderedEndTimeInput[classIndex] == '') return
    this.renderedClasses[classIndex].classSchedules.push({
      dayOfWeek: this.renderedDayOfWeekInput[classIndex],
      startTime: this.renderedStartTimeInput[classIndex],
      endTime: this.renderedEndTimeInput[classIndex]
    });
    this.renderedDayOfWeekInput[classIndex] = '';
    this.renderedStartTimeInput[classIndex] = '';
    this.renderedEndTimeInput[classIndex] = '';

  }


  deleteClassSchedule(classIndex: number, scheduleIndex: number) {
    this.renderedClasses[classIndex].classSchedules.splice(scheduleIndex, 1);

  }

  addExtraClass() {
    this.renderedDayOfWeekInput.push('');
    this.renderedStartTimeInput.push('');
    this.renderedEndTimeInput.push('');
    this.renderedClasses.push({
      name: "اسم الصف",
      staff: {
        id: ""
      },
      classSchedules: [],
      gradeClassAssignments: []
    })

  }

  deleteClass(classIndex: number) {
    this.renderedEndTimeInput.splice(classIndex, 1);
    this.renderedDayOfWeekInput.splice(classIndex, 1);
    this.renderedStartTimeInput.splice(classIndex, 1);
    this.renderedClasses.splice(classIndex, 1);

  }

  addGradeToClass(classIndex: number, grade: string) {
    if (grade == '') return
    this.renderedClasses[classIndex].gradeClassAssignments.push({ grade })
    const index = this.grades.indexOf(grade);
    if (index !== -1) {
      this.grades.splice(index, 1);
    }
    this.selectedGrade = '';
  }

  removeGradeFromClass(classIndex: number, grade: string) {
    if (grade == '') return
    const index = this.renderedClasses[classIndex].gradeClassAssignments.findIndex((g: any) => g.grade === grade);
    if (index !== -1) {
      this.renderedClasses[classIndex].gradeClassAssignments.splice(index, 1);
    }
    this.grades.push(grade);
  }

  getGradeLabel(grade: string): string {
    switch (grade) {
      case 'FIRST': return 'الصف الأول';
      case 'SECOND': return 'الصف الثاني';
      case 'THIRD': return 'الصف الثالث';
      case 'FOURTH': return 'الصف الرابع';
      case 'FIFTH': return 'الصف الخامس';
      case 'SIXTH': return 'الصف السادس';
      case 'SEVENTH': return 'الصف الأول إعدادي';
      case 'EIGHTH': return 'الصف الثاني إعدادي';
      case 'NINTH': return 'الصف الثالث إعدادي';
      case 'TENTH': return 'الصف الأول ثانوي';
      case 'ELEVENTH': return 'الصف الثاني الثانوي';
      case 'TWELFTH': return 'الصف الثالث الثانوي';
      default: return 'صف غير معروف';
    }

  }

  getDayLabel(day: string): string {
    switch (day) {
      case 'SUNDAY': return 'الأحد';
      case 'MONDAY': return 'الاثنين';
      case 'TUESDAY': return 'الثلاثاء';
      case 'WEDNESDAY': return 'الأربعاء';
      case 'THURSDAY': return 'الخميس';
      case 'FRIDAY': return 'الجمعة';
      case 'SATURDAY': return 'السبت';
      default: return 'يوم غير معروف';
    }
  }

  convertToArabicTime(time: string): string {
    if (!time) return '';
    const [hourStr, minute] = time.split(':');
    let hour = parseInt(hourStr, 10);
    const isPM = hour >= 12;
    const period = isPM ? 'م' : 'ص';
    hour = hour % 12 || 12;
    const formattedTime = `${hour}:${minute} ${period}`;
    return this.toArabicNumerals(formattedTime);
  }

  toArabicNumerals(str: string): string {
    return str.replace(/\d/g, d => '٠١٢٣٤٥٦٧٨٩'[parseInt(d)]);
  }



}
