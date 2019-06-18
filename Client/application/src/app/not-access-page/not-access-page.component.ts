import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-not-access-page',
  templateUrl: './not-access-page.component.html',
  styleUrls: ['./not-access-page.component.css']
})
export class NotAccessPageComponent implements OnInit {

  url: string;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    if (this.route.snapshot.params['url']) {
      this.url = this.route.snapshot.params['url'];  
    } 
    else {
      this.url = null; 
    }
    
  }

}
