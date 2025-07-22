import { Routes } from '@angular/router';
import { LoginComponent } from './staff/login/login.component';
import { LoginComponent as StudentLoginComponent } from './students/login/login.component';
import { staffGuard } from './guards/staff.guard';
import { RegisterComponent } from './staff/admin/register/register.component';
import { RegisterComponent as StudentRegisterComponent } from './students/register/register.component';
import { roleGuard } from './guards/role.guard';
import { studentGuard } from './guards/student.guard';

export const routes: Routes = [
    { path: 'staff/login', component: LoginComponent },
    {
        path: 'staff/admin/register',
        component: RegisterComponent,
        canActivate: [staffGuard, roleGuard],
        data: { role: 'ADMIN' }
    },

    { path: 'login', component: StudentLoginComponent },
    { path: 'register', component: StudentRegisterComponent },

];
