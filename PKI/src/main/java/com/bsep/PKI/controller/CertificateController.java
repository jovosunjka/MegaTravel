package com.bsep.PKI.controller;

import com.bsep.PKI.dto.CertificateSigningRequest;
import com.bsep.PKI.model.*;
import com.bsep.PKI.repository.UserRepository;
import com.bsep.PKI.service.CertificateService;
import com.bsep.PKI.service.certificate.CertificateGeneratorService;
import com.bsep.PKI.service.keystore.KeyStoreReaderService;
import com.bsep.PKI.service.keystore.KeyStoreWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCertificate(@RequestBody CertificateSigningRequest csr) {
        X509Certificate certificate = null;

        try {
            certificateService.createCertificate(csr);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }



        return new ResponseEntity(certificate, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public ResponseEntity<String> sendCertificate() {
        X509Certificate certificate = null;
        HttpEntity<X509Certificate> httpEntity = new HttpEntity<X509Certificate>(certificate);
        String destinationUrl = "https://localhost:8081/api/certificate/receive";
        ResponseEntity<Void> responseEntity = restTemplate.exchange(destinationUrl, HttpMethod.POST, httpEntity, Void.class);

        String message;
        if (responseEntity.getStatusCode() != HttpStatus.OK) message = "Sertifikat nije uspesno poslat!";
        else message = "Sertifikat je uspesno poslat!";

        System.out.println(message);
        return new ResponseEntity(message, responseEntity.getStatusCode());
    }

    @RequestMapping(value = "/is-revoked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isRevoked(@RequestParam("serialNumber") Long serialNumber) {
        boolean retValue = false;
        try {
            retValue = certificateService.isRevoked(serialNumber);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(retValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/cao", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cao() {
        return new ResponseEntity<String>("Cao druze", HttpStatus.OK);
    }

    @RequestMapping(value = "/database", method = RequestMethod.GET)
    public ResponseEntity database() {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new User("pera", "pera", new RootCA( "localhost", "MegaTravel",
                "MegaTravelRoot", "RS", "http://localhost:8080/pki/certificate/create")));
        users.add(new User("zika", "zika", new IntermediateCA("789456", "localhost", "MegaTravel",
                "MegaTravelRoot", "RS", "http://localhost:8080/pki/certificate/create")));
        userRepository.saveAll(users);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/windows-agent", method = RequestMethod.GET)
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
