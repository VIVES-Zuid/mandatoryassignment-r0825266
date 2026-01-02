import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { TaskListComponent } from './task-list/task-list.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'projects', pathMatch: 'full' },

    { path: 'login', component: LoginComponent },

    {
        path: 'projects',
        component: ProjectListComponent,
        canActivate: [authGuard]
    },

    {
        path: 'projects/:projectId/tasks',
        component: TaskListComponent,
        canActivate: [authGuard]
    },

    { path: '**', redirectTo: 'projects' }
];
