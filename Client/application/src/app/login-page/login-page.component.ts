import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {
  public user;
  public wrongUsernameOrPass: boolean;

  constructor(private authenticationService: AuthenticationService, private router: Router,
              private toastr: ToastrService) {
    this.user = {};
    this.wrongUsernameOrPass = false;
  }

  ngOnInit() {
    if (this.authenticationService.isLoggedIn()) {
        const currentUser: any = this.authenticationService.getCurrentUser();
        this.goToPageOfLoggedUser(currentUser);
    }
  }

  login(): void {
    this.authenticationService.login(this.user.username, this.user.password)
    .subscribe((loggedIn: boolean) => {
      console.log(loggedIn);
      if (loggedIn) {
        const currentUser: any = this.authenticationService.getCurrentUser();
        this.toastr.success('Successfully logged in as ' + currentUser.username);
        this.goToPageOfLoggedUser(currentUser);
      } else {
        this.toastr.error('Unsuccessfully login :(');
      }
    },
    (err: Error) => {
      if (err.toString() === 'Ilegal login') {
        this.wrongUsernameOrPass = true;
        console.log(err);
      } else {
        Observable.throw(err);
      }
      this.toastr.error(JSON.stringify(err));
    });
  }

  goToPageOfLoggedUser(currentUser: any) {
      const roles: any[] = currentUser.roles;

      if (roles || roles.length > 0) {
        if (roles.includes('SECURE_ADMINISTRATOR')) {
          this.router.navigate(['/send-message']);
        } else {
          this.toastr.error('Unknown user type!');
        }
      }
      else {
        this.toastr.error('Backend did not return any jwt token!');
      }
  }

}
