import { Routes } from '@angular/router';
import { MainPageComponent } from './main-page/main-page.component';
import { authGuard } from './guards/auth.guard';
import { NgxGaugeModule } from 'ngx-gauge';
import { importProvidersFrom } from '@angular/core';

export const routes: Routes = [
    // Main Page
    {
        path: '',
        component: MainPageComponent,
        canActivate: [authGuard],
        data: { accessControlled: false }
    },

    // Student Routes
    {
        path: 'login',
        loadComponent: () => import('./students/login/login.component').then(c => c.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () => import('./students/register/register.component').then(c => c.RegisterComponent)
    },
    {
        path: 'student',
        loadComponent: () => import('./students/student/student.component').then(c => c.StudentComponent),
        canActivate: [authGuard],
        data: { role: 'STUDENT', accessControlled: true }
    },

    // Staff Login
    {
        path: 'staff/login',
        loadComponent: () => import('./staff/login/login.component').then(c => c.LoginComponent)
    },

    // Admin Routes
    {
        path: 'staff/admin/register',
        loadComponent: () => import('./staff/admin/staff/register/register.component').then(c => c.RegisterComponent),
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },
    {
        path: 'staff/admin/staff',
        loadComponent: () => import('./staff/admin/staff/staff.component').then(c => c.StaffComponent),
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },
    {
        path: 'staff/admin/semesters',
        loadComponent: () => import('./staff/admin/semesters/semesters.component').then(c => c.SemestersComponent),
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },
    {
        path: 'staff/admin/semesters/create',
        loadComponent: () => import('./staff/admin/semesters/create/create.component').then(c => c.CreateComponent),
        canActivate: [authGuard],
        data: { role: 'ADMIN', accessControlled: true }
    },

    // Instructor Routes
    {
        path: 'staff/instructor/topics',
        loadComponent: () => import('./staff/instructor/topics/topics.component').then(c => c.TopicsComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/upcoming-sessions',
        loadComponent: () => import('./staff/instructor/upcoming-sessions/upcoming-sessions.component').then(c => c.UpcomingSessionsComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes',
        loadComponent: () => import('./staff/instructor/assigned-classes/assigned-classes.component').then(c => c.AssignedClassesComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes/assignments/create',
        loadComponent: () => import('./staff/instructor/assigned-classes/assignments/create-assignment/create-assignment.component').then(c => c.CreateAssignmentComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes/assignments/submit',
        loadComponent: () => import('./staff/instructor/assigned-classes/assignments/submit-assignment/submit-assignment.component').then(c => c.SubmitAssignmentComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },
    {
        path: 'staff/instructor/assigned-classes/attendance',
        loadComponent: () => import('./staff/instructor/upcoming-sessions/attendance/attendance.component').then(c => c.AttendanceComponent),
        canActivate: [authGuard],
        data: { role: 'INSTRUCTOR', accessControlled: true }
    },

    // Supervisor Routes
    {
        path: 'staff/supervisor/transfer-student',
        loadComponent: () => import('./staff/supervisor/transfer-student/transfer-student.component').then(c => c.TransferStudentComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/cancel-sessions',
        loadComponent: () => import('./staff/supervisor/cancel-sessions/cancel-sessions.component').then(c => c.CancelSessionsComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/classes',
        loadComponent: () => import('./staff/supervisor/classes/classes.component').then(c => c.ClassesComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/classes/assignments/create',
        loadComponent: () => import('./staff/supervisor/classes/create-assignment/create-assignment.component').then(c => c.CreateAssignmentComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/classes/assignments/submit',
        loadComponent: () => import('./staff/instructor/assigned-classes/assignments/submit-assignment/submit-assignment.component').then(c => c.SubmitAssignmentComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/announcements',
        loadComponent: () => import('./staff/supervisor/announcements/announcements.component').then(c => c.AnnouncementsComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    },
    {
        path: 'staff/supervisor/announcements/create',
        loadComponent: () => import('./staff/supervisor/announcements/create/create.component').then(c => c.CreateComponent),
        canActivate: [authGuard],
        data: { role: 'SUPERVISOR', accessControlled: true }
    }

];
