import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';


export interface Toast {
  message: string;
  type: 'success' | 'error';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject = new Subject<Toast | null>();
  toastState$ = this.toastSubject.asObservable();

  show(message: string, type: 'success' | 'error' = 'success') {
    this.toastSubject.next({ message, type });
  }

  clear() {
    this.toastSubject.next(null);
  }
}