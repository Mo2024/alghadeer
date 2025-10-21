import { Component, Input } from '@angular/core';
import { EditComponent } from './edit/edit.component';
import { CreateComponent } from './create/create.component';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../../services/toast.service';
import { SalahService } from '../../../../../services/salah.service';
import { environment } from '../../../../../../environments/environment';

@Component({
  selector: 'app-subjects',
  imports: [CommonModule, CreateComponent, EditComponent],
  templateUrl: './subjects.component.html',
  styleUrl: './subjects.component.css'
})
export class SubjectsComponent {
  subjects: any;

  @Input() isArea: boolean = false
  @Input() showAddSubject: boolean = false
  @Input() showEditSubject: boolean = false
  @Input() subjectId: any;
  @Input() areaId: any;
  @Input() subjectName: any;

  constructor(private toastService: ToastService, private salahService: SalahService) { }

  ngOnInit() {

    this.salahService.getSubjects().subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          console.log('a')
          this.subjects = res
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

  toggleAddSubjectOpen(isArea: boolean, subjectId?: number) {
    this.subjectId = subjectId ? subjectId : null;
    this.isArea = isArea;
    this.toggleAddSubject();
  }

  toggleAddSubject() {
    this.showAddSubject = !this.showAddSubject;
  }

  openAddAreaModal(subjectId: number) {
    alert(`فتح نافذة إضافة موضوع فرعي للموضوع رقم ${subjectId}`);
  }

  editArea(area: any) {
    alert(`تعديل الموضوع الفرعي: ${area.name}`);
  }

  deleteArea(subId: number, subjectIndex: number, areaIndex: number) {


    // this.areaService.deleteArea({ id: subId }).subscribe({
    //   next: async (res) => {
    //     if (!environment.production) {
    //       console.log(res)
    //     }

    //     if (res) {
    //       this.subjects[subjectIndex].areas.splice(areaIndex, 1);
    //     } else {
    //       this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
    //     }

    //   },
    //   error: (error) => {
    //     if (!environment.production) {
    //       console.log(error)
    //     }

    //     if (error.error.status === "ALGD-400") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-403") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-500") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-409") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else {
    //       this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
    //     }
    //   }
    // })

  }

  deleteSubject(subjectId: number, subjectIndex: number) {
    // this.subjectsService.deleteSubject({ id: subjectId }).subscribe({
    //   next: async (res) => {
    //     if (!environment.production) {
    //       console.log(res)
    //     }

    //     if (res) {
    //       this.subjects.splice(subjectIndex, 1);
    //     } else {
    //       this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
    //     }

    //   },
    //   error: (error) => {
    //     if (!environment.production) {
    //       console.log(error)
    //     }

    //     if (error.error.status === "ALGD-400") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-403") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-500") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else if (error.error.status === "ALGD-409") {
    //       this.toastService.show(error.error.message, 'error');
    //     } else {
    //       this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
    //     }
    //   }
    // })

  }

  handleSubjectAdded(subjects: any) {
    this.subjects = subjects
    this.showAddSubject = false;
  }

  toggleEditSubjectOpen(isArea: boolean, subjectName: string, subjectId?: number, areaId?: number,) {
    console.log(subjectId)
    this.subjectName = subjectName;
    this.subjectId = subjectId ? subjectId : null;
    this.areaId = areaId ? areaId : null;
    this.isArea = isArea;
    this.toggleEditSubject()
  }

  toggleEditSubject() {
    this.showEditSubject = !this.showEditSubject;

  }

  handleSubjectEdited(subjects: any) {
    this.subjects = subjects
    console.log(subjects)
    this.showAddSubject = false;
  }

  trackBySubjectId(index: number, subject: any): number {
    return subject.id;
  }

  trackByAreaId(index: number, area: any): number {
    return area.id;
  }

}
