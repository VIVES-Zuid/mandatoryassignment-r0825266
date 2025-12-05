/*import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WarehouseItemService {

  //private apiUrl = 'http://localhost:5000/api/warehouse-items';
  private apiUrl = 'http://backend:5000/api/warehouse-items';

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<any> {
    return this.http.get(this.apiUrl);
  }
}*/




import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { WarehouseItem } from '../models/warehouse-item';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WarehouseItemService {

  private apiUrl = environment.apiUrl;
  //private apiUrl = `${environment.apiUrl}/warehouse-items`;

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<WarehouseItem[]> {
    return this.http.get<WarehouseItem[]>(this.apiUrl).pipe(
      tap(result => console.log("Fetched warehouse items:", result))
    );
  }
}
