import { Injectable, Injector } from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse} from '@angular/common/http';
import { AuthenticationService } from '../authentication.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

import { from, Observable } from 'rxjs';
import { concatMap, map } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
//import { KeycloakService } from '../keycloak/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService /*implements HttpInterceptor*/ {
  
  /*
  constructor(private auth: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const tokenPromise: Promise<string> = this.auth.getToken();
    const tokenObservable: Observable<string> = from(tokenPromise);

    return tokenObservable.pipe(
      map(authToken => {
        req = req.clone({setHeaders: {'Authorization': 'Bearer ' + authToken}});
      }),
      concatMap(request => {
        return next.handle(req);
      }));
  }*/

  /*constructor(private kcService: KeycloakService) {}
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      const authToken = this.kcService.getToken() || '';
      request = request.clone({
        setHeaders: {
          'Authorization': 'Bearer ' + authToken
        }
      });
      return next.handle(request);
    }*/


  /*
    constructor(private injector: Injector, private router: Router) { }


  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authenticationService: AuthenticationService = this.injector.get(AuthenticationService);
    
    request = request.clone({
      setHeaders: { 'X-Auth-Token': `${authenticationService.getToken()}`}
    });

    return next.handle(request).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401 || err.status === 403) { // Unauthorized || Forbidden
            console.log('err.error =', err.error, ';');
            authenticationService.logout(); // brisemo token ako je postojao i istekao
                                            // i teramo korisnika da se ponovo uloguje
            this.router.navigate(['/login']);
            throwError(err);
          }
          
        }

        return throwError(err);
      })
    );

  }*/

}
