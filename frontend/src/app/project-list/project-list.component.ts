import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectService } from '../services/project.service';
import { Project } from '../models/project';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit {

  projects: Project[] = [];

  constructor(
    private projectService: ProjectService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.projectService.getProjects().subscribe({
      next: page => {
        this.projects = page.content;
      },
      error: err => console.error('Failed to load projects', err)
    });
  }

  openProject(projectId: number): void {
    console.log('Clicked project:', projectId);
    this.router.navigate(['/projects', projectId, 'tasks']);
  }


  logout(): void {
    if (confirm('Are you sure you want to log out?')) {
      localStorage.clear();
      this.router.navigate(['/login']);
    }
  }
}