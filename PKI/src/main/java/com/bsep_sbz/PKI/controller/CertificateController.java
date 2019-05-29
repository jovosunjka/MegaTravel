package com.bsep_sbz.PKI.controller;

import com.bsep_sbz.PKI.config.ftp.MyGateway;
import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.model.CertificateType;
import com.bsep_sbz.PKI.model.IntermediateCA;
import com.bsep_sbz.PKI.model.RootCA;
import com.bsep_sbz.PKI.model.User;
import com.bsep_sbz.PKI.repository.UserRepository;
import com.bsep_sbz.PKI.service.CertificateService;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
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

    @Autowired
    private MyGateway gateway;

    //@Autowired
    //private FTPSClient ftpsClient;


    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCertificate(@RequestBody CertificateSigningRequest csr) {
        X509Certificate certificate = null;

        try {
            certificate = certificateService.createCertificate(csr);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }



        return new ResponseEntity(certificate, HttpStatus.CREATED);
    }

    //@RequestMapping(value = "/send", method = RequestMethod.GET)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<String> sendCertificate() {
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjR5ZccKEIbuUA/MUVoYkcaqFimsLKvR2KmaxVbMojPb9rSXQnnFBTDscvsXMfaY4Ezv3k33K4NzVNCfOE6wqJRFN70dO5VVVyhupsuyxcsfrDPZ8vhmuNjl/FGUNGEIv5LV+6BzkXUDeAP4w1zOLMXUcUQv2cT0GKtMu53tT02qvLhWTJs8F7YL172gPDz3Hggp/l8oriBazhMO43diyYUVfK8dRgKHHCyuIg0JPTuz7w2WuRooiSnROiO19C1FUUjJGQvmZjYLhLcgY89XNQOvQiKKh3ipxX4VhtOy5uDARmrUoB7SBlIIvDqOK1c3G7kUuuofK6X2IMtKqA21y9QIDAQAB";
        CertificateSigningRequest csr = new CertificateSigningRequest("test", "test", "test", "RS", "12345", publicKeyStr, CertificateType.OTHER, null);
        X509Certificate certificate = null;
        try {
            certificate = certificateService.createCertificate(csr);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String certificateStr = null;
        try {
            certificateStr = Base64.encodeBase64String(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> httpEntity = new HttpEntity<String>(certificateStr);
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

    //@RequestMapping(value = "/send-truststore", method = RequestMethod.PUT)
    @EventListener(ApplicationReadyEvent.class)
    public /*ResponseEntity*/void sendTruststore() {
        System.out.println("Saljem...");
        gateway.sendToFtp(new File("C:\\bsep_sbz_workspace\\MegaTravel\\PKI\\src\\main\\resources\\foo\\bar.txt"));
        System.out.println("Poslao");

        /*try {
            ftpsClient.connect(ftpHost, ftpPort);
            ftpsClient.login(ftpUsername, ftpPassword);

            if (ftpsClient.isConnected()) {
                System.out.println("Uspesno smo se ulogovali!");
            }
            else {
                System.out.println("Nismo uspeli da se ulogujemo!");
            }
            ftpsClient.logout();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpsClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        //return new ResponseEntity(HttpStatus.OK);
    }
}
