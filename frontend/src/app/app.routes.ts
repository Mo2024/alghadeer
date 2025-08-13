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
import { UpcomingSessionsComponent } from './staff/instructor/upcoming-sessions/upcoming-sessions.component';
import { AssignedClassesComponent } from './staff/instructor/assigned-classes/assigned-classes.component';
import { AttendanceComponent } from './staff/instructor/upcoming-sessions/attendance/attendance.component';
import { CancelSessionsComponent } from './staff/supervisor/cancel-sessions/cancel-sessions.component';
import { ClassesComponent } from './staff/supervisor/classes/classes.component';

export const routes: Routes = [
    { path: '', component: MainPageComponent, canActivate: [authGuard], data: { accessControlled: false } },

    // Staff Components
    { path: 'staff/login', component: LoginComponent, canActivate: [authGuard], data: { accessControlled: false } },

    // Admin Components
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

    // Instructor Components
    {
        path: 'staff/instructor/topics',
        component: TopicsComponent,
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/upcoming-sessions',
        component: UpcomingSessionsComponent,
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes',
        component: AssignedClassesComponent,
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes/attendance',
        component: AttendanceComponent,
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },

    // Supervisor Components
    {
        path: 'staff/supervisor/transfer-student',
        component: TransferStudentComponent,
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/cancel-sessions',
        component: CancelSessionsComponent,
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/classes',
        component: ClassesComponent,
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },

    // Student Components
    { path: 'login', component: StudentLoginComponent, canActivate: [authGuard], data: { accessControlled: false } },
    { path: 'register', component: StudentRegisterComponent, canActivate: [authGuard], data: { accessControlled: false } },
    {
        path: 'student',
        component: StudentComponent,
        canActivate: [authGuard],
        data: { role: "STUDENT", accessControlled: true }
    },

];


