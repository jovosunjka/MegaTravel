import { Component, OnInit, Inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TrustStoreConfig } from '../model/trust-store-config';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-send-message',
  templateUrl: './send-message.component.html',
  styleUrls: ['./send-message.component.css']
})
export class SendMessageComponent implements OnInit {

  trustStoreConfigs: TrustStoreConfig[];
  currentTrustStoreConfig: TrustStoreConfig;
  relativeUrlTrustStoreConfigs: string = '/certificate/get-trust-store-configs';
  relativeUrlSaveTrustStoreConfigs: string = '/certificate/save-and-send-truststore';

  constructor(private http: HttpClient, private toastr: ToastrService, 
              @Inject('PKI_URL') private baseUrl: string) {

  }

  ngOnInit() {
    this.getTruststoreConfigs();
  }

  /*save() {
    if (this.message !== '') {
      const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
      return this.http.post('https://localhost:8443/pki/certificate/windows-agent', this.message, {headers}).subscribe (
        result => alert('Message ' + this.message + ' was sent.')
      );
    } else {
      alert('Message ' + this.message + ' was not sent.' );
    }
  }*/

  save() {
      const headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
      this.http.put(this.baseUrl + this.relativeUrlSaveTrustStoreConfigs, this.trustStoreConfigs, { headers }).subscribe (
        () => this.toastr.success('You have successfully saved the truststore configurations!'),
        () => this.toastr.error('You have failed to save truststore configurations!')
      );
    
  }

  getTruststoreConfigs() {
    this.http.get<TrustStoreConfig[]>(this.baseUrl + this.relativeUrlTrustStoreConfigs).subscribe(
      (trustStoreConfigs: TrustStoreConfig[]) => {
        this.trustStoreConfigs = trustStoreConfigs;
        if (this.trustStoreConfigs) {
          if (this.trustStoreConfigs.length > 0) {
            this.toastr.success('Trust store configs are successfully loaded!');
            this.currentTrustStoreConfig = this.trustStoreConfigs[0];
          }
          else {
            this.toastr.warning('There are no trust store configs at the moment!');
          }
        }
        else {
          this.toastr.error('Problem with loading trust store configs !');
        }
      },
      //err => this.toastr.error('Error: ' + JSON.stringify(err))
    );
  }

  changedState(organizationalUnitName: string) {
      const index = this.currentTrustStoreConfig.trustStoreCertificateOrganizationalUnitNames.indexOf(organizationalUnitName, 0);
      if (index > -1) {
        this.currentTrustStoreConfig.trustStoreCertificateOrganizationalUnitNames.splice(index, 1);
      }  
      else {
        this.currentTrustStoreConfig.trustStoreCertificateOrganizationalUnitNames.push(organizationalUnitName);
      }

      console.log(this.trustStoreConfigs);
  }

}
