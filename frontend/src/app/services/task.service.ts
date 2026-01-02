import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Page } from '../models/page';
import { Task } from '../models/task';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  getTasksForProject(projectId: number, page = 0, size = 20, status?: string): Observable<Page<Task>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (status) params = params.set('status', status);

    return this.http.get<Page<Task>>(`${this.apiUrl}/projects/${projectId}/tasks`, { params });
  }
}
