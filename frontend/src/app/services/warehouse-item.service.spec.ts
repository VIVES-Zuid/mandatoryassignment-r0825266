import { TestBed } from '@angular/core/testing';

import { WarehouseItemService } from './warehouse-item.service';

describe('WarehouseItemService', () => {
  let service: WarehouseItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WarehouseItemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
