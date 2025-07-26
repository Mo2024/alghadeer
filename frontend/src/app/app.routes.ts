import { Routes } from '@angular/router';
import { LoginComponent } from './staff/login/login.component';
import { LoginComponent as StudentLoginComponent } from './students/login/login.component';
import { RegisterComponent, RegisterComponent as StudentRegisterComponent } from './students/register/register.component';
import { StudentComponent } from './students/student/student.component';
import { authGuard } from './guards/auth.guard';
import { MainPageComponent } from './main-page/main-page.component';
import { StaffComponent } from './staff/admin/staff/staff.component';
import { SemestersComponent } from './staff/admin/semesters/semesters.component';

export const routes: Routes = [
    { path: '', component: MainPageComponent, canActivate: [authGuard], data: { accessControlled: false } },

    { path: 'staff/login', component: LoginComponent, canActivate: [authGuard], data: { accessControlled: false } },
    {
        path: 'staff/admin/register',
        component: RegisterComponent,
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },
    {
        path: 'staff/admin/staff',
        component: StaffComponent,
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },
    {
        path: 'staff/admin/semesters',
        component: SemestersComponent,
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },

    { path: 'login', component: StudentLoginComponent, canActivate: [authGuard], data: { accessControlled: false } },
    { path: 'register', component: StudentRegisterComponent, canActivate: [authGuard], data: { accessControlled: false } },
    {
        path: 'student',
        component: StudentComponent,
        canActivate: [authGuard],
        data: { role: "STUDENT", accessControlled: true }
    },

];


