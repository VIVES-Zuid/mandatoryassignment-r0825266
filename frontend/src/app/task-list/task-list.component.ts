import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { TaskService } from '../services/task.service';
import { Task } from '../models/task';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {
  projectId!: number;
  tasks: Task[] = [];
  total = 0;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('projectId'));

    this.taskService.getTasksForProject(this.projectId).subscribe({
      next: page => {
        this.tasks = page.content;
        this.total = page.totalElements;
      },
      error: err => console.error('Failed to load tasks', err)
    });
  }

  back(): void {
    this.location.back(); // goes back to projects page
  }
}
