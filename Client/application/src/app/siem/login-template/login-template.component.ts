import { Component, OnInit } from '@angular/core';
import {LoginTemplate} from '../../model/logintemplate';
import {Relation} from '../../model/enum/relation';
import {TimeUnit} from '../../model/enum/timeunit';
import {ToastrService} from 'ngx-toastr';
import {GenericService} from '../../services/generic.service';

@Component({
  selector: 'app-login-template',
  templateUrl: './login-template.component.html',
  styleUrls: ['./login-template.component.css']
})
export class LoginTemplateComponent implements OnInit {
  template: LoginTemplate;
  Relation = Relation; // html need this to access Relation enum
  TimeUnit = TimeUnit;
  isLoaderVisible: boolean;

  constructor(private toast: ToastrService, private genericService: GenericService) {
    this.template = {} as LoginTemplate;
    this.isLoaderVisible = false;
  }

  ngOnInit() {
  }

  save() {
    if (!this.areValuesValid()) {
      this.toast.warning('All fields must be filled');
      return;
    }

    this.isLoaderVisible = true;
    this.genericService.post('/logs/template', this.template).subscribe(() => {
        this.toast.info('New login template successfully saved');
        this.isLoaderVisible = false;
      }, error => {
        console.log(JSON.stringify(error));
        this.toast.error('Server error, please try later');
        this.isLoaderVisible = false;
      }
    );
  }

  private areValuesValid(): boolean {
    const t: LoginTemplate = this.template;
    return !(t.loginSuccess === undefined || t.loginAttemptCount === undefined || t.sourceRelation === undefined
      || t.hostRelation === undefined || t.timeCount === undefined || t.timeUnit === undefined);
  }
}
