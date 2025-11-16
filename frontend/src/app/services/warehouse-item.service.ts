import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WarehouseItemService {

  private apiUrl = 'http://localhost:5000/api/warehouse-items';

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<any> {
    return this.http.get(this.apiUrl);
  }
}
