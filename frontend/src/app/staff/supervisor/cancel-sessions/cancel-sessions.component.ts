import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../../services/semester/session.service';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-cancel-sessions',
    imports: [CommonModule, FormsModule],
    templateUrl: './cancel-sessions.component.html',
    styleUrl: './cancel-sessions.component.css'
})
export class CancelSessionsComponent {

  constructor(private route: ActivatedRoute, private sessionService: SessionService, private toastService: ToastService) { }

  cancelType: string | null = null;

  sessions: any

  assignedVariable: any = '' // this is used for both dates and class id
  removedSession: Map<any, any> = new Map<any, any>();
  sessionsToCancel: any = []

  private queryParamSubscription: Subscription | undefined;

  isDisabled: boolean = false;

  ngOnDestroy() {
    if (this.queryParamSubscription) {
      this.queryParamSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.queryParamSubscription = this.route.queryParamMap.subscribe(params => {
      const newType = params.get('type');

      if (newType !== 'bySession' && newType !== 'byDate') {
        this.toastService.show("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", 'error');
      }

      if (newType !== this.cancelType) {
        this.cancelType = newType;
        this.assignedVariable = '';
        this.removedSession.clear();
        this.sessionsToCancel = []
        console.log('Type changed to:', this.cancelType);
      }

    });
    this.getSessionsData()
  }

  getSessionsData() {
    if (this.cancelType == "byDate") {
      this.sessionService.getSessionsDates().subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
            console.log(res.length)
          }

          if (res) {
            this.sessions = [...res];
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
    } else if (this.cancelType == "bySession") {
      this.sessionService.getSessionsByActiveSemester().subscribe({
        next: async (res) => {
          if (!environment.production) {
            console.log(res)
            console.log(res.length)

          }

          if (res) {
            this.sessions = [...res];
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
  }

  onSubmit() {
    if (this.sessionsToCancel.length <= 0) {
      this.toastService.show('يرجى التأكد من تعبئة جميع الحقول', 'error');
      return;
    }
    this.isDisabled = true;

    if (this.cancelType == "byDate") {
      this.sessionService.cancelSessionsByDates(this.sessionsToCancel).subscribe({
        next: async (res) => {
          this.isDisabled = false;
          if (!environment.production) {
            console.log(res)
          }

          this.sessionsToCancel = [];
          this.assignedVariable = '';
          this.removedSession.clear();
          this.toastService.show(res.message, 'success');
          this.sessions = this.getSessionsData();

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
    } else if (this.cancelType == "bySession") {
      this.sessionService.cancelSessionsBySessionId(this.sessionsToCancel).subscribe({
        next: async (res) => {
          this.isDisabled = false;
          if (!environment.production) {
            console.log(res)
          }

          this.sessionsToCancel = [];
          this.assignedVariable = '';
          this.removedSession.clear();
          this.toastService.show(res.message, 'success');
          this.sessions = this.getSessionsData();

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
  }
  addSession(index: number) {
    if (this.cancelType == "byDate") {
      const date = this.sessions[index]
      this.removedSession.set(date, date)
      this.sessionsToCancel.push(date)
      this.sessions.splice(index, 1);
      this.assignedVariable = ''
    } else if (this.cancelType == "bySession") {
      const session = { ...this.sessions[index] }


      this.removedSession.set(session.id, session)
      this.sessionsToCancel.push(session.id)
      this.sessions.splice(index, 1);
      this.assignedVariable = ''
    }


  }

  removeSession(index: number) {
    const session = this.removedSession.get(this.sessionsToCancel[index]);

    this.removedSession.delete(this.sessionsToCancel[index])
    this.sessionsToCancel.splice(index, 1)
    this.sessions.push(session)
    this.assignedVariable = ''
  }

  getSession(index: any) {
    return this.removedSession.get(this.sessionsToCancel[index]);
  }
}
