import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DbLogsComponent } from './db-logs.component';

describe('DbLogsComponent', () => {
  let component: DbLogsComponent;
  let fixture: ComponentFixture<DbLogsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DbLogsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DbLogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
