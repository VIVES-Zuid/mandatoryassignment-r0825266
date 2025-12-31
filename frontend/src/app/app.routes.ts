import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'projects' },
    { path: 'login', component: LoginComponent },
    { path: 'projects', component: ProjectListComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'projects' },
];