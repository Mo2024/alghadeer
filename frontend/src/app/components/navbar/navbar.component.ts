import { Component } from '@angular/core';
import { StaffService } from '../../services/staff.service';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { PermissionsService } from '../../services/permissions.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(public permissionsService: PermissionsService, public router: Router) { }

  ngOnInit() {
    if (!environment.production) {
      console.log(this.permissionsService.getPermissions());
      console.log(this.permissionsService.hasPermission('ADMIN'));
    }
  }

  isStaffRoute(): boolean {
    return this.router.url.includes('/staff/');
  }
}
