import {Component, OnInit, Inject, OnDestroy} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {ToastrService} from 'ngx-toastr';
import {Alarm} from '../../model/alarm';
import {GenericService} from '../../services/generic.service';

@Component({
  selector: 'app-alarms',
  templateUrl: './alarms.component.html',
  styleUrls: ['./alarms.component.css']
})
export class AlarmsComponent implements OnInit, OnDestroy {
  serverSocketRelativeUrl = '/socket';
  stompClient;
  successfullyConnected: boolean;
  subscription: any;
  alarms: Alarm[];
  page: number;
  size: number;
  isLastPage: boolean;
  isFirstPage: boolean;
  numOfTotalPages: number;

  constructor(@Inject('SIEM_CENTER_API_URL') private baseUrl: string,
              private toast: ToastrService,
              protected genericService: GenericService) {
    this.alarms = [];
    this.size = 10;
    this.page = 0;
    this.isLastPage = true;
    this.isFirstPage = true;
  }

  ngOnInit() {
    this.getDbAlarms();
    this.initializeWebSocketConnection();
  }

  ngOnDestroy() {
    if (this.stompClient) {
      if (this.successfullyConnected) {
        this.subscription.unsubscribe();
        this.stompClient.disconnect();
      }
    }
  }

  initializeWebSocketConnection() {
    const socket = new SockJS(this.baseUrl + this.serverSocketRelativeUrl);
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({},
      () =>  {
        this.successfullyConnected = true;
        this.subscription = this.stompClient.subscribe(`/topic`, (message) => {
          if (message.body) {
            this.toast.error('New alarm raised!');
            this.getDbAlarms();
          }
          else {
            console.log('Empty body in a websocket message!');
          }
        });
      },
      () => this.successfullyConnected = false
    );
  }

  getDbAlarms() {
    this.genericService.get(`/logs/alarms?page=${this.page}&size=${this.size}`).subscribe(res => {
      this.alarms = res.content;
      this.isLastPage = res.last;
      this.isFirstPage = res.first;
      this.numOfTotalPages = res.totalPages;
    }, error => {
      JSON.stringify(error);
      this.toast.error(error.error);
    });
  }

  getNextPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page++;
    this.getDbAlarms();
  }

  getPreviousPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page--;
    this.getDbAlarms();
  }

  getFirstPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page = 0;
    this.getDbAlarms();
  }

  getLastPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.page = this.numOfTotalPages - 1;
    this.getDbAlarms();
  }

  getCurrentPage() {
    if (!this.areValuesValid()) {
      return;
    }
    this.getDbAlarms();
  }

  private areValuesValid(): boolean {
    if (this.size < 1) {
      this.toast.warning('Size can not be less than 1');
      return false;
    }

    return true;
  }

  getDate(timestamp: number): string {
    const split = String(timestamp).split(',');
    return `${split[0]}/${split[1]}/${split[2]} ${split[3]}:${split[4]}:${split[5]}`;
  }
}
