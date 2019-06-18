import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { ToastrService } from 'ngx-toastr';


// https://medium.com/@blained3/connecting-keycloak-to-angular-d175c92a0dd3
// https://medium.com/@sairamkrish/keycloak-integration-part-2-integration-with-angular-frontend-f2716c696a28
// https://www.npmjs.com/package/keycloak-angular



@Injectable({
  providedIn: 'root'
})
export class MyKeycloakService {
  //private keycloakAuth: any;

  constructor(private keycloak: KeycloakService, private toastr: ToastrService) { }

 
  public init(): Promise<any> {
    return new Promise((resolve, reject) => {
      const configObj = {
        'url': 'https://localhost:8444/auth',
        'realm': 'demo',
        'clientId': 'client-app'
      };

      //this.keycloakAuth = new Keycloak(config);
      this.keycloak.init({ 
        config: configObj,
        initOptions: {
          onLoad: 'login-required',
          checkLoginIframe: false
        },
        enableBearerInterceptor: true, // ovo ce u svaki request ubaciti sledeci header 
                                      //=>  'Authorization': 'Bearer ' + authToken
                                      // radi se o AccessToken-u koji je izgenerisao Keycloak 
        ////bearerExcludedUrls: ['/assets', '/clients/public']
      })
        .then(() => {
          resolve();
        })
        .catch(() => {
          reject();
        });
      });
  }

  logout(): void {
    this.keycloak.logout('https://localhost:4200').then(
      () => this.toastr.info('Successfully logout!')
    ).catch(
      () => this.toastr.error('Problem with logout!')
    );
  }

  getToken(): string {
    const token: string = '';
    this.keycloak.getToken();

    return token;
  }
}



/*import { Injectable } from '@angular/core';


// https://medium.com/@blained3/connecting-keycloak-to-angular-d175c92a0dd3
// https://medium.com/@sairamkrish/keycloak-integration-part-2-integration-with-angular-frontend-f2716c696a28
// https://www.npmjs.com/package/keycloak-angular

declare var Keycloak: any;


@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloakAuth: any;

  constructor() { }

 
  public init(): Promise<any> {
  return new Promise((resolve, reject) => {
      const config = {
        'url': 'https://localhost:8444/auth',
        'realm': 'demo',
        'clientId': 'client-app'
      };
      this.keycloakAuth = new Keycloak(config);
      this.keycloakAuth.init({ onLoad: 'login-required'})
        .success(() => {
          resolve();
        })
        .error(() => {
          reject();
        });
      });
  }

  getToken(): string {
    return this.keycloakAuth.token;
  }
}*/
