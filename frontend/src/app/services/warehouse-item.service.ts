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
import { Observable, map, tap } from 'rxjs';
import { WarehouseItem } from '../models/warehouse-item';

const API_URL = 'http://localhost:5000/api/warehouse-items';

@Injectable({
  providedIn: 'root'
})
export class WarehouseItemService {

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<WarehouseItem[]> {
    return this.http.get<WarehouseItem[]>(API_URL)
      .pipe(
        tap(result => console.log("Fetched warehouse items:", result))
      );
  }
}

