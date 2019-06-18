import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { SendMessageComponent } from './send-message/send-message.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NotFoundPageComponent } from './not-found-page/not-found-page.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { AuthenticationService } from './services/authentication.service';
import { JwtUtilsService } from './services/jwt-utils.service';
import { GenericService } from './services/generic.service';
import { ToastrModule } from 'ngx-toastr';
import { TokenInterceptorService } from './services/token-interceptor-service/token-interceptor.service';
import { RbacPageComponent } from './rbac-page/rbac-page.component';
import { DbLogsComponent } from './siem/db-logs/db-logs.component';
import { LoginTemplateComponent } from './siem/login-template/login-template.component';
import { SessionLogsComponent } from './siem/session-logs/session-logs.component';
import { LogsComponent } from './siem/logs/logs.component';
import { AlarmsComponent } from './siem/alarms/alarms.component';
import { AppAuthGuard } from './guard/app-auth.guard';
import { NotAccessPageComponent } from './not-access-page/not-access-page.component';
import { MyKeycloakService } from './services/keycloak/my-keycloak.service';
import { KeycloakAngularModule } from 'keycloak-angular';


// ova fabrika ce pokrenuti nasu init metodu iz KeycloakService
export function kcFactory(myKeycloakService: MyKeycloakService) {
  return () => myKeycloakService.init();
}
/*export function kcFactory(keycloakService: AuthService) {
  return () => keycloakService.init();
}*/


const appRoutes: Routes = [
  { path: 'send-message', 
    component: SendMessageComponent,
    canActivate: [AppAuthGuard],
    data: { 
      expectedRoles: ['SECURE_ADMINISTRATOR']
    }  
  },
  { path: 'rbac', 
    component: RbacPageComponent,
    canActivate: [AppAuthGuard],
    data: { 
      expectedRoles: ['SECURE_ADMINISTRATOR']
    } 
  },
  //{ path: 'login', component: LoginPageComponent},
  { path: 'dblogs', 
  component: DbLogsComponent,
  canActivate: [AppAuthGuard],
  data: { 
      expectedRoles: ['ADMINISTRATOR', 'OPERATER']
    }
  },
  { path: 'session_logs',
    component: SessionLogsComponent,
    canActivate: [AppAuthGuard],
    data: { 
      expectedRoles: ['ADMINISTRATOR', 'OPERATER']
    }
  },
  { path: 'login_template', 
    component: LoginTemplateComponent,
    canActivate: [AppAuthGuard],
    data: { 
      expectedRoles: ['ADMINISTRATOR', 'OPERATER']
    }
  },
  { path: 'alarms', 
    component: AlarmsComponent,
    canActivate: [AppAuthGuard],
    data: { 
      expectedRoles: ['ADMINISTRATOR', 'OPERATER']
    }
  },
  { path: '', // localhost:4200 redirect to localhost:4200/login
    //redirectTo: '/login',
    redirectTo: '/send-message',
    pathMatch: 'full'
  },
  { path: 'not-access-page/:url', component: NotAccessPageComponent },
  { path: '**', component: NotFoundPageComponent } // za sve ostale path-ove izbaci page not found
];

@NgModule({
  declarations: [
    AppComponent,
    SendMessageComponent,
    //LoginPageComponent,
    NotFoundPageComponent,
    RbacPageComponent,
    DbLogsComponent,
    SessionLogsComponent,
    LogsComponent,
    LoginTemplateComponent,
    SessionLogsComponent,
    LogsComponent,
    AlarmsComponent,
    NotAccessPageComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule, // required animations module
    ToastrModule.forRoot({preventDuplicates: true}), // ToastrModule added
    KeycloakAngularModule
  ],
  providers: [
    GenericService,
    AuthenticationService,
    JwtUtilsService,
    //{provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true },
    { provide: 'SIEM_CENTER_API_URL', useValue: 'https://localhost:8081/api' },  // siem center
    { provide: 'PKI_URL', useValue: 'https://localhost:8443/pki' },  // pki
    MyKeycloakService,
    //AuthService,
    {
      // APP_INITIALIZER ce prilikom pokretanjaove aplikacije, koristeci kcFactory pokrenuti nasu init metodu iz KeycloakService
      provide: APP_INITIALIZER, 
      useFactory: kcFactory,
      deps: [MyKeycloakService],
      multi: true
    },
    AppAuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
