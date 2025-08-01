import { Routes } from '@angular/router';
import { LoginComponent } from './staff/login/login.component';
import { LoginComponent as StudentLoginComponent } from './students/login/login.component';
import { RegisterComponent as StudentRegisterComponent } from './students/register/register.component';
import { RegisterComponent as StaffRegisterComponent } from './staff/admin/staff/register/register.component';
import { StudentComponent } from './students/student/student.component';
import { authGuard } from './guards/auth.guard';
import { MainPageComponent } from './main-page/main-page.component';
import { StaffComponent } from './staff/admin/staff/staff.component';
import { SemestersComponent } from './staff/admin/semesters/semesters.component';
import { CreateComponent } from './staff/admin/semesters/create/create.component';
import { TopicsComponent } from './staff/instructor/topics/topics.component';
import { TransferStudentComponent } from './staff/supervisor/transfer-student/transfer-student.component';

export const routes: Routes = [
    { path: '', component: MainPageComponent, canActivate: [authGuard], data: { accessControlled: false } },

    { path: 'staff/login', component: LoginComponent, canActivate: [authGuard], data: { accessControlled: false } },
    {
        path: 'staff/admin/register',
        component: StaffRegisterComponent,
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
    {
        path: 'staff/admin/semesters/create',
        component: CreateComponent,
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },

    {
        path: 'staff/instructor/topics',
        component: TopicsComponent,
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },

    {
        path: 'staff/supervisor/transfer-student',
        component: TransferStudentComponent,
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
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


