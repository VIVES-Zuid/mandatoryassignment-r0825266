import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Project } from '../models/project';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) { }

  getProjects(): Observable<Project[]> {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get<Project[]>(this.apiUrl, { headers });
  }
}
