import { Routes } from '@angular/router';
import { LoginComponent } from './staff/login/login.component';
import { LoginComponent as StudentLoginComponent } from './students/login/login.component';
import { RegisterComponent } from './staff/admin/register/register.component';
import { RegisterComponent as StudentRegisterComponent } from './students/register/register.component';
import { StudentComponent } from './students/student/student.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: 'staff/login', component: LoginComponent },
    {
        path: 'staff/admin/register',
        component: RegisterComponent,
        canActivate: [authGuard],
        data: { role: 'ADMIN' }
    },

    { path: 'login', component: StudentLoginComponent },
    { path: 'register', component: StudentRegisterComponent },
    {
        path: 'student',
        component: StudentComponent,
        canActivate: [authGuard],
        data: { role: "STUDENT" }
    },

];
