import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../services/toast.service';
import { ClassService } from '../../../../services/semester/class.service';
import { environment } from '../../../../../environments/environment';
import { AnnouncementService } from '../../../../services/semester/announcement.service';

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {

  announcementTypes = [
    { value: 'INFO', label: 'معلومة' },
    { value: 'ALERT', label: 'تنبيه' },
    { value: 'EVENT', label: 'فعالية' },
    { value: 'REMINDER', label: 'تذكير' }
  ];

  classes: any
  classesCopy: any

  announcement: any = {
    announcementType: '',
    content: '',
    startDate: '',
    endDate: '',
    general: false,
    announcementTargets: []
  };

  classIndex: string = '';
  isDisabled = false;

  removedClasses: Map<number, any> = new Map<number, any>();

  constructor(private toastService: ToastService, private classService: ClassService, private announcementService: AnnouncementService) { }

  ngOnInit() {
    this.classService.getClassesByActiveSemester().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.classes = [...res];
          this.classesCopy = [...res];
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

  onSubmit() {
    const allowedTypes = this.announcementTypes.map(t => t.value);
    if (!allowedTypes.includes(this.announcement.announcementType)) {
      this.toastService.show('يجب اختيار نوع إعلان صحيح', 'error');
      return;
    }

    const arabicRegex = /^[\u0600-\u06FF0-9\s.,!?؛،:()'"-]+$/;
    if (!this.announcement.content || !arabicRegex.test(this.announcement.content)) {
      this.toastService.show('المحتوى يجب أن يكون باللغة العربية', 'error');
      return;
    }

    if (!this.announcement.startDate) {
      this.toastService.show('يرجى إدخال تاريخ البداية', 'error');
      return;
    }

    if (!this.announcement.endDate) {
      this.toastService.show('يرجى إدخال تاريخ النهاية', 'error');
      return;
    }

    const start = new Date(this.announcement.startDate);
    const end = new Date(this.announcement.endDate);
    if (end <= start) {
      this.toastService.show('تاريخ النهاية يجب أن يكون بعد تاريخ البداية', 'error');
      return;
    }

    if (!this.announcement.general) {
      if ((!this.announcement.announcementTargets || this.announcement.announcementTargets.length === 0)) {
        this.toastService.show('يجب اختيار صف واحد على الأقل', 'error');
        return;
      }
    }

    this.announcementService.createAnnouncement(this.announcement).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.announcement.announcementType = '';
          this.announcement.content = '';
          this.announcement.startDate = '';
          this.announcement.endDate = '';
          this.announcement.general = false;
          this.announcement.announcementTargets = [];

          this.classes = [...this.classesCopy];
          this.removedClasses.clear()


          this.toastService.show(res.message, 'success');
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

  addClass(classIndex: any) {
    const class_ = { ...this.classes[classIndex] };

    this.classes.splice(classIndex, 1)
    this.removedClasses.set(class_.id, class_)
    this.announcement.announcementTargets.push({
      semesterClass: {
        id: class_.id
      }
    })
    this.classIndex = '';

  }

  removeClass(index: any) {
    const classId = this.announcement.announcementTargets[index].semesterClass.id
    const class_ = this.removedClasses.get(classId);

    this.classes.push(class_);
    this.removedClasses.delete(classId);
    this.announcement.announcementTargets.splice(index, 1)

  }


}
