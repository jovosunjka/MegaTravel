import { Component, OnInit } from '@angular/core';
import {Log} from '../../model/log';
import {ToastrService} from 'ngx-toastr';
import {GenericService} from '../../services/generic.service';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {
  logs: Log[];
  page: number;
  size: number;
  isLastPage: boolean;
  isFirstPage: boolean;
  numOfTotalPages: number;
  regExp: string;
  column: string;
  inputName: string;
  urlSuffix: string;

  constructor(protected toast: ToastrService, protected genericService: GenericService) {
    this.logs = [];
    this.size = 10;
    this.page = 0;
    this.isLastPage = true;
    this.isFirstPage = true;
    this.regExp = '';
    this.column = '';
  }

  ngOnInit() {

  }

  protected initialize(inputNameParam: string, urlSuffixParam: string) {
    this.inputName = inputNameParam;
    this.urlSuffix = urlSuffixParam;
  }

  getNextPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page++;
    this.getLogs();
  }

  getPreviousPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page--;
    this.getLogs();
  }

  getFirstPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page = 0;
    this.getLogs();
  }

  getLastPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page = this.numOfTotalPages - 1;
    this.getLogs();
  }

  getCurrentPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.getLogs();
  }

  getDate(timestamp: number): string {
    return new Date(timestamp).toLocaleString();
  }

  private getLogs() {
    this.genericService.post(this.getLogsUrlSuffix(), {'regExp': this.regExp, 'column': this.column }).subscribe(res => {
      this.logs = res.content;
      if (this.logs.length === 0) {
        this.toast.warning('No elements found');
      }
      this.isLastPage = res.last;
      this.isFirstPage = res.first;
      this.numOfTotalPages = res.totalPages;
    }, error => {
      JSON.stringify(error);
      this.toast.error(error.error);
    });
  }

  private areValuesValid(): boolean {
    if (this.size < 1) {
      this.toast.warning('Size can not be less than 1');
      return false;
    }
    if (this.regExp === '') {
      this.toast.warning('Field(s) can not be empty');
      return false;
    }
    if (this.column === '') {
      this.toast.warning('Field(s) can not be empty');
      return false;
    }

    return true;
  }

  private getLogsUrlSuffix(): string {
    return `/logs/${this.urlSuffix}?page=${this.page}&size=${this.size}`;
  }
}
