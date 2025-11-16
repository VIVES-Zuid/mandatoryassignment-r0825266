import { Component, OnInit } from '@angular/core';
import { WarehouseItemService } from './services/warehouse-item.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  items: any[] = [];

  constructor(private warehouseService: WarehouseItemService) { }

  ngOnInit() {
    this.loadItems();
  }

  loadItems() {
    this.warehouseService.getAllItems().subscribe(data => {
      this.items = data;
    });
  }
}
