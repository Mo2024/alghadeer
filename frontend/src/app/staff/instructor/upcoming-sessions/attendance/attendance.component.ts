import { Component } from '@angular/core';
import { SessionService } from '../../../../services/semester/session.service';
import { environment } from '../../../../../environments/environment';
import { ToastService } from '../../../../services/toast.service';
import { NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.css'
})
export class AttendanceComponent {

  constructor(private sessionService: SessionService, private toastService: ToastService, private router: Router) { }

  sessionObject: any;
  statusObject: any;

  private routerSub: any;


  ngOnInit(): void {
    this.sessionObject = this.sessionService.getSession();
    console.log(this.sessionObject)

    this.routerSub = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.sessionService.clearSession();
      }
    });

    this.sessionObject = this.sessionService.getSession();

    this.sessionService.getAttendanceStatus({
      sessionId: this.sessionObject.id,
      classId: this.sessionObject.class.id
    }).subscribe({
      next: async (res) => {
        if (!environment.production) {
          console.log(res)
        }

        if (res) {
          this.statusObject = res;
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
  ngOnDestroy() {
    this.routerSub.unsubscribe();
  }

}
