import { Component, OnInit } from '@angular/core';
import {LogsComponent} from '../logs/logs.component';
import {ToastrService} from 'ngx-toastr';
import {GenericService} from '../../services/generic.service';

@Component({
  selector: 'app-session-logs',
  templateUrl: '../logs/logs.component.html',
  styleUrls: ['../logs/logs.component.css']
})
export class SessionLogsComponent extends LogsComponent implements OnInit {

  constructor(protected toast: ToastrService, protected genericService: GenericService) {
    super(toast, genericService);
  }

  ngOnInit() {
    super.initialize('Filter', 'session');
  }

}
