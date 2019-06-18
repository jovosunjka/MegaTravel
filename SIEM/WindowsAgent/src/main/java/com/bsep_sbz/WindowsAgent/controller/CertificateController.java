package com.bsep_sbz.WindowsAgent.controller;


import com.bsep_sbz.WindowsAgent.controller.dto.CertificateSigningRequest;
import com.bsep_sbz.WindowsAgent.service.interfaces.ICertificateService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${ca-url}")
    private String caUrl;


    /**
     * Ova metoda ce traziti od svog CA-a da joj izgenerise sertifikat, pa ce dobijeni sertifikat sacuvati u keystore.
     * Ima li smisla da metod bude POST ??? Ipak ce kao rezultat ovog metoda biti dobijanje novog resursa na ovom serveru,
     * sto je valjda i smisao POST metoda.
     * @return
     */
    @RequestMapping(value = "/send-request", method = RequestMethod.POST)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity sendRequestForCertificate() {
        KeyPair keyPair = certificateService.generateKeyPair();
        CertificateSigningRequest csr = certificateService.prepareCSR(keyPair.getPublic());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(caUrl, csr, String.class);
        if(responseEntity.getStatusCode() == HttpStatus.CREATED) {
            String certificateStr = responseEntity.getBody();
            X509Certificate certificate = certificateService.getCertificate(certificateStr);
            try {
                certificateService.saveCertificate(keyPair.getPrivate(), certificate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity(responseEntity.getStatusCode());
    }

    @RequestMapping(value = "/receive", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity receiveCertificateForTrustStore(@RequestBody String certificateStr) {
        X509Certificate certificate = certificateService.getCertificate(certificateStr);
        try {
            certificateService.saveCertificateInTrustStore(certificate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/cao", method = RequestMethod.GET)
    public ResponseEntity cao() {
        KeyPair keyPair = certificateService.generateKeyPair();
        String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        String privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        certificateService.loadCertificate("./src/main/resources/stores/MegaTravelSiemCenter2.cer");

        return  new ResponseEntity(HttpStatus.OK);
    }

    //@RequestMapping(value = "/windows-agent", method = RequestMethod.GET)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<String> communicateWithWindowsAgent() {
        //ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://localhost:8082/api/bez-veze/poruka", new String("Cao Windows Agente"), String.class);
        HttpEntity<String> httpEntity = new HttpEntity<String>(new String("Cao Windows Agente"));
        ResponseEntity<String> responseEntity = restTemplate.exchange("https://localhost:8082/api/bez-veze/poruka",
                HttpMethod.POST,httpEntity, String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            String poruka = responseEntity.getBody();
            System.out.println(poruka);
        }

        return new ResponseEntity<String>("Komunikacija uspesno izvrsena", HttpStatus.OK);
    }
}
