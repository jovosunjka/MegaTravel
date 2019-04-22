import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RbacPageComponent } from './rbac-page.component';

describe('RbacPageComponent', () => {
  let component: RbacPageComponent;
  let fixture: ComponentFixture<RbacPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RbacPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RbacPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
