import { Component, OnInit } from '@angular/core';
import {Log} from '../../model/log';
import {ToastrService} from 'ngx-toastr';
import {GenericService} from '../../services/generic.service';

@Component({
  selector: 'app-db-logs',
  templateUrl: './db-logs.component.html',
  styleUrls: ['./db-logs.component.css']
})
export class DbLogsComponent implements OnInit {
  logs: Log[];
  page: number;
  size: number;
  isLastPage: boolean;
  isFirstPage: boolean;
  numOfTotalPages: number;
  regExp: string;
  column: string;

  constructor(private toast: ToastrService, private genericService: GenericService) {
    this.logs = [];
    this.size = 10;
    this.page = 0;
    this.isLastPage = true;
    this.isFirstPage = true;
    this.regExp = '.*';
    this.column = 'message';
  }

  ngOnInit() {
    this.getLogs();
  }

  getNextPage() {
    this.checkValues();
    this.page++;
    this.getLogs();
  }

  getPreviousPage() {
    this.checkValues();
    this.page--;
    this.getLogs();
  }

  getFirstPage() {
    this.checkValues();
    this.page = 0;
    this.getLogs();
  }

  getLastPage() {
    this.checkValues();
    this.page = this.numOfTotalPages - 1;
    this.getLogs();
  }

  getCurrentPage() {
    this.checkValues();
    this.getLogs();
  }

  getDate(timestamp: number): string {
    return new Date(timestamp).toLocaleString();
  }

  private getLogs() {
    this.genericService.post(this.getLogsUrlSuffix(), {'regExp': this.regExp, 'column': this.column }).subscribe(res => {
      this.logs = res.content;
      this.isLastPage = res.last;
      this.isFirstPage = res.first;
      this.numOfTotalPages = res.totalPages;
    });
  }

  private checkValues() {
    if (this.size < 1) {
      this.toast.warning('Size can not be less than 1');
      return;
    }
  }

  private getLogsUrlSuffix(): string {
    return `/logs/filter?page=${this.page}&size=${this.size}`;
  }
}
