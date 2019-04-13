package com.bsep.SIEMCenter.controller;


import com.bsep.SIEMCenter.controller.dto.CertificateSigningRequest;
import com.bsep.SIEMCenter.service.interfaces.ICertificateService;
import com.bsep.SIEMCenter.service.interfaces.IRestTemplateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @Autowired
    private IRestTemplateWrapper restTemplateWrapper;


    @Value("${ca-url}")
    private String caUrl;


    /**
     * Ova metoda ce traziti od svog CA-a da joj izgenerise sertifikat, pa ce dobijeni sertifikat sacuvati u keystore.
     * Ima li smisla da metod bude POST ??? Ipak ce kao rezultat ovog metoda biti dobijanje novog resursa na ovom serveru,
     * sto je valjda i smisao POST metoda.
     * @return
     */
    @RequestMapping(value = "/send-request", method = RequestMethod.POST)
    public ResponseEntity sendRequestForCertificate() {
        KeyPair keyPair = certificateService.generateKeyPair();
        CertificateSigningRequest csr = certificateService.prepareCSR(keyPair.getPublic());

        RestTemplate restTemplate = restTemplateWrapper.get();
        ResponseEntity<X509Certificate> responseEntity = restTemplate.postForEntity(caUrl, csr, X509Certificate.class);
        if(responseEntity.getStatusCode() == HttpStatus.CREATED) {
            X509Certificate certificate = responseEntity.getBody();
            certificateService.saveCertificate(keyPair.getPrivate(), certificate);
        }

        return new ResponseEntity(responseEntity.getStatusCode());
    }

    @RequestMapping(value = "/receive", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity receiveCertificateForTrustStore(@RequestBody X509Certificate certificate) {
        certificateService.saveCertificateInTrustStore(certificate);
        return  new ResponseEntity(HttpStatus.OK);
    }
}
