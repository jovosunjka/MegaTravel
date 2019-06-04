import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
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



const appRoutes: Routes = [
  { path: 'send-message', component: SendMessageComponent},
  { path: 'rbac', component: RbacPageComponent},
  { path: 'login', component: LoginPageComponent},
  { path: 'dblogs', component: DbLogsComponent},
  { path: 'login_template', component: LoginTemplateComponent},
  { path: '', // localhost:4200 redirect to localhost:4200/login
    redirectTo: '/login',
    pathMatch: 'full'
  },
  { path: '**', component: NotFoundPageComponent } // za sve ostale path-ove izbaci page not found
];

@NgModule({
  declarations: [
    AppComponent,
    SendMessageComponent,
    LoginPageComponent,
    NotFoundPageComponent,
    RbacPageComponent,
    DbLogsComponent,
    LoginTemplateComponent
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
  ],
  providers: [
    GenericService,
    AuthenticationService,
    JwtUtilsService,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true },
    { provide: 'SIEM_CENTER_API_URL', useValue: 'https://localhost:8081/api' },  // siem center
    { provide: 'PKI_URL', useValue: 'https://localhost:8443/pki' },  // pki
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
