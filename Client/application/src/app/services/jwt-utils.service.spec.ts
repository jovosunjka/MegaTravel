import { TestBed } from '@angular/core/testing';

import { JwtUtilsService } from './jwt-utils.service';

describe('JwtUtilsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: JwtUtilsService = TestBed.get(JwtUtilsService);
    expect(service).toBeTruthy();
  });
});
