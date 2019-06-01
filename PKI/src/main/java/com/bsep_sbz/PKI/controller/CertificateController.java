package com.bsep_sbz.PKI.controller;

import com.bsep_sbz.PKI.dto.CertificateRevocationRequest;
import com.bsep_sbz.PKI.dto.CertificateRevocationResponse;
import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.dto.TrustStoreConfigDTO;
import com.bsep_sbz.PKI.model.*;
import com.bsep_sbz.PKI.repository.UserRepository;
import com.bsep_sbz.PKI.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    private static final String MAIN_COMPANY_UNIT = "MegaTravelRootCA";
    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCertificate(@RequestBody CertificateSigningRequest csr) {
        X509Certificate certificate = null;

        try {
            certificate = certificateService.createCertificate(csr);
            certificateService.saveCertificate(certificate);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<String> sendCertificate() {
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjR5ZccKEIbuUA/MUVoYkcaqFimsLKvR2KmaxVbMojPb9rSXQnnFBTDscvsXMfaY4Ezv3k33K4NzVNCfOE6wqJRFN70dO5VVVyhupsuyxcsfrDPZ8vhmuNjl/FGUNGEIv5LV+6BzkXUDeAP4w1zOLMXUcUQv2cT0GKtMu53tT02qvLhWTJs8F7YL172gPDz3Hggp/l8oriBazhMO43diyYUVfK8dRgKHHCyuIg0JPTuz7w2WuRooiSnROiO19C1FUUjJGQvmZjYLhLcgY89XNQOvQiKKh3ipxX4VhtOy5uDARmrUoB7SBlIIvDqOK1c3G7kUuuofK6X2IMtKqA21y9QIDAQAB";
        CertificateSigningRequest csr = new CertificateSigningRequest("test", "test", "test", "RS", "12345", publicKeyStr, CertificateType.OTHER, null);
        X509Certificate certificate = null;
        try {
            certificate = certificateService.createCertificate(csr);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

    @RequestMapping(value = "/is-revoked", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity isRevoked(@RequestBody CertificateRevocationRequest request) {
        try {
            boolean retValue = certificateService.isRevoked(request.getSerialNumber());
            return new ResponseEntity<>(new CertificateRevocationResponse(retValue, new Date()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/revoke", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity revoke(@RequestBody CertificateRevocationRequest request) {
        try {
            boolean retValue = certificateService.isRevoked(request.getSerialNumber());
            if(retValue) {
                return new ResponseEntity<>("Certificate is already revoked", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        certificateService.revoke(request.getSerialNumber());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/cao", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cao() {
        return new ResponseEntity<String>("Cao druze", HttpStatus.OK);
    }

    /*@RequestMapping(value = "/database", method = RequestMethod.GET)
    public ResponseEntity database() {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new User("pera", "pera", new RootCA( "localhost", "MegaTravel",
                "MegaTravelRoot", "RS", "http://localhost:8080/pki/certificate/create")));
        users.add(new User("zika", "zika", new IntermediateCA("789456", "localhost", "MegaTravel",
                "MegaTravelRoot", "RS", "http://localhost:8080/pki/certificate/create")));
        userRepository.saveAll(users);
        return new ResponseEntity(HttpStatus.CREATED);
    }*/

    @RequestMapping(value = "/windows-agent", method = RequestMethod.GET)
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

    @RequestMapping(value = "/save-and-send-truststore", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity sendTruststore(@RequestBody List<TrustStoreConfigDTO> trustStoreConfigDtos) {
        List<Certificate> nonRevokedCertificates = certificateService.getNonRevokedCertiificates();

        trustStoreConfigDtos.parallelStream()
            .filter(tsConfigDTO -> {
                    ChangedTrustStoreConfig changedTrustStoreConfig = certificateService.isChangedTrustStoreConfig(tsConfigDTO, nonRevokedCertificates);
                    if(changedTrustStoreConfig.isChanged()) {
                        // samo one trustStore konfiguracije koje su promenjene za odredjeni sertifikat (aplikaciju),
                        // se cuvaju u bazi
                        certificateService.saveCertificate(changedTrustStoreConfig.getCertificate());
                    }

                    return changedTrustStoreConfig.isChanged();
                }
            )
            .forEach(tsConfigDTO -> {
                    // samo one trustStore konfiguracije koje su promenjene za odredjeni sertifikat (aplikaciju),
                    // se salju toj aplikaciji koristeci ftp
                    List<String> tscOrganizationalUnitNames = tsConfigDTO.getTrustStoreCertificateOrganizationalUnitNames();
                    // svaka aplikacija treba da moze pricati preko HTTPS i sa PKI-jem
                    tscOrganizationalUnitNames.add(MAIN_COMPANY_UNIT);
                    System.out.println("Saljem "+tsConfigDTO.getOrganizationalUnitName()+"-u njegov truststore...");

                    File trustStoreFile = null;
                    try {
                        trustStoreFile = certificateService.prepareTrustStoreFile(tsConfigDTO.getOrganizationalUnitName(), tscOrganizationalUnitNames);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        certificateService.sendFile(trustStoreFile, tsConfigDTO.getOrganizationalUnitName());
                        System.out.println("Poslao "+tsConfigDTO.getOrganizationalUnitName()+"-u njegov truststore.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            );

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-trust-store-configs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<List<TrustStoreConfigDTO>> getTruststoreConfigs() {
        List<Certificate> certificates = certificateService.getNonRevokedCertiificates();
        List<TrustStoreConfigDTO> trustStoreConfigDTOS = certificates.stream()
                .filter(cer -> !cer.getOrganizationalUnitName().equalsIgnoreCase(MAIN_COMPANY_UNIT))
                // iskljucujemo root ca-ov sertifikat
                .map(cer -> new TrustStoreConfigDTO(cer.getOrganizationalUnitName(),
                        cer.getTrustStoreCertificates().stream()
                                        .map(c -> c.getOrganizationalUnitName())
                                        .collect(Collectors.toList())
                        )
                )
                .collect(Collectors.toList());
        return new ResponseEntity<List<TrustStoreConfigDTO>>(trustStoreConfigDTOS, HttpStatus.OK);
    }
}
