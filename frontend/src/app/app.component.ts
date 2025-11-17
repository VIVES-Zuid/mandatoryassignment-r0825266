import { Component, OnInit } from '@angular/core';
import { WarehouseItemService } from './services/warehouse-item.service';
import { WarehouseItem } from './models/warehouse-item';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  items: WarehouseItem[];

  constructor(private warehouseService: WarehouseItemService) {
    this.items = [];
  }

  ngOnInit() {
    this.loadItems();
  }

  loadItems() {
    this.warehouseService.getAllItems().subscribe({
      next: (items: WarehouseItem[]) => {
        console.log('Items received from service:', items);
        this.items = items;
      },
      error: (err) => console.error('Failed to load items', err)
    });
  }
}
