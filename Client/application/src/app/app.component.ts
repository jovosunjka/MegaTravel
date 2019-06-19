import { Component } from '@angular/core';
import { AuthenticationService } from './services/authentication.service';
import { MyKeycloakService } from './services/keycloak/my-keycloak.service';
import { AuthService } from './services/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'application';

  constructor(public myKeycloakService: MyKeycloakService) {

  }

  /*constructor(public authService: AuthService) {

  }*/

}
