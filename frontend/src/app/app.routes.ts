import { Routes } from '@angular/router';
import { LoginComponent } from './staff/login/login.component';
import { LoginComponent as StudentLoginComponent } from './students/login/login.component';
import { staffGuard } from './guards/staff.guard';
import { RegisterComponent } from './staff/admin/register/register.component';
import { RegisterComponent as StudentRegisterComponent } from './students/register/register.component';
import { studentGuard } from './guards/student.guard';
import { StudentComponent } from './students/student/student.component';

export const routes: Routes = [
    { path: 'staff/login', component: LoginComponent },
    {
        path: 'staff/admin/register',
        component: RegisterComponent,
        canActivate: [staffGuard],
        data: { role: 'ADMIN' }
    },

    { path: 'login', component: StudentLoginComponent },
    { path: 'register', component: StudentRegisterComponent },
    {
        path: 'student',
        component: StudentComponent,
        canActivate: [studentGuard],
        data: { role: "STUDENT" }
    },

];
