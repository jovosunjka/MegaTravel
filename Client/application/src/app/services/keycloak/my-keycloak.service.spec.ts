import { TestBed } from '@angular/core/testing';

import { MyKeycloakService } from './my-keycloak.service';

describe('MyKeycloakService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MyKeycloakService = TestBed.get(MyKeycloakService);
    expect(service).toBeTruthy();
  });
});
