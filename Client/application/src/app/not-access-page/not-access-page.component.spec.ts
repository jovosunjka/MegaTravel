import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotAccessPageComponent } from './not-access-page.component';

describe('NotAccessPageComponent', () => {
  let component: NotAccessPageComponent;
  let fixture: ComponentFixture<NotAccessPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotAccessPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotAccessPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
