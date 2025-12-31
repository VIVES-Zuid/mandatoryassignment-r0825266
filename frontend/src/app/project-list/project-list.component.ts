import { Component, OnInit } from '@angular/core';
import { ProjectService } from '../services/project.service';
import { Project } from '../models/project';

@Component({
  selector: 'app-project-list',
  imports: [],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit {

  projects: Project[] = [];

  constructor(private projectService: ProjectService) { }

  ngOnInit(): void {
    this.projectService.getProjects().subscribe({
      next: data => this.projects = data,
      error: err => console.error('Failed to load projects', err)
    });
  }
}
