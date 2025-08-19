import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ToastComponent } from './components/toast/toast.component';
import { Toast, ToastService } from './services/toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, ToastComponent, CommonModule],
  providers: [ToastService],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(private toastService: ToastService) { }
  // mohdosama2030@gmail.com
  ngOnInit() {
    this.toastService.toastState$.subscribe((toast: Toast | null) => {
      if (toast) {
        this.toastMessage = toast.message;
        this.toastType = toast.type;
      } else {
        this.toastMessage = ''; // clear display only
      }
    });
  }
}