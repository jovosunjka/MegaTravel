import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';


@Component({
  selector: 'app-send-message',
  templateUrl: './send-message.component.html',
  styleUrls: ['./send-message.component.css']
})
export class SendMessageComponent implements OnInit {
  message: string;

  constructor(private http: HttpClient) { 
    this.message = '';
  }

  ngOnInit() {
  }

  save() {
    if (this.message !== '') {
      const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
      return this.http.post("http://localhost:8081/api/logs/process", this.message, {headers}).subscribe(
        result => { alert("Message " + this.message + " was sent.");
          }      )
    }
    else {
      alert("Message " + this.message + " was not sent." );
    }
  
}
}
