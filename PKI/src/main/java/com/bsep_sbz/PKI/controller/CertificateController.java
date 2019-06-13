package com.bsep_sbz.PKI.controller;

import com.bsep_sbz.PKI.dto.CertificateRevocationRequest;
import com.bsep_sbz.PKI.dto.CertificateRevocationResponse;
import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.dto.TrustStoreConfigDTO;
import com.bsep_sbz.PKI.model.*;
import com.bsep_sbz.PKI.service.CertificateService;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.security.provider.certpath.OCSP;
import sun.security.provider.certpath.OCSPResponse;

import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    @Value("${main_company_unit}")
    private String MainCompanyUnit;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCertificate(@RequestBody CertificateSigningRequest csr) {
        X509Certificate certificate = null;

        try {
            certificate = certificateService.createCertificate(csr, CertificateType.OTHER);
            //certificateService.saveCertificate(certificate);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        String certificateStr = null;
        try {
            certificateStr = Base64.encodeBase64String(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>(certificateStr, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    //@EventListener(ApplicationReadyEvent.class)
    public ResponseEntity<String> sendCertificate() {
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjR5ZccKEIbuUA/MUVoYkcaqFimsLKvR2KmaxVbMojPb9rSXQnnFBTDscvsXMfaY4Ezv3k33K4NzVNCfOE6wqJRFN70dO5VVVyhupsuyxcsfrDPZ8vhmuNjl/FGUNGEIv5LV+6BzkXUDeAP4w1zOLMXUcUQv2cT0GKtMu53tT02qvLhWTJs8F7YL172gPDz3Hggp/l8oriBazhMO43diyYUVfK8dRgKHHCyuIg0JPTuz7w2WuRooiSnROiO19C1FUUjJGQvmZjYLhLcgY89XNQOvQiKKh3ipxX4VhtOy5uDARmrUoB7SBlIIvDqOK1c3G7kUuuofK6X2IMtKqA21y9QIDAQAB";
        CertificateSigningRequest csr = new CertificateSigningRequest("test", "test", "test", "RS", "12345", publicKeyStr, null);
        X509Certificate certificate = null;
        try {
            certificate = certificateService.createCertificate(csr, CertificateType.OTHER);
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

    @RequestMapping(value = "/is-revoked/{serial-number}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity isRevoked(@PathVariable("serial-number") long serialNumber) {
        try {
            boolean retValue = certificateService.isRevoked(serialNumber);
            //return new ResponseEntity<>(new CertificateRevocationResponse(retValue, new Date()), HttpStatus.OK);
            return new ResponseEntity<>(retValue, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*@RequestMapping(value = "/is-revoked", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity isRevoked(@RequestBody OCSPReq ocspReq) {
        try {
            JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider( "BC" );

            X509CertificateHolder[] certificateHolders = ocspReq.getCerts();

            X509Certificate certificate0 = converter.getCertificate(certificateHolders[0]);
            X509Certificate certificate1 = converter.getCertificate(certificateHolders[1]);
            OCSP.RevocationStatus revocationStatus = OCSP.check(certificate0, certificate1);
            return new ResponseEntity<>(revocationStatus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }*/

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

        trustStoreConfigDtos.stream()
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
                    System.out.println("Saljem "+tsConfigDTO.getOrganizationalUnitName()+"-u njegov truststore...");

                    File trustStoreFile = null;
                    try {
                        trustStoreFile = certificateService.prepareTrustStoreFile(tsConfigDTO.getOrganizationalUnitName(), tscOrganizationalUnitNames, null);
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
                .filter(cer -> !cer.getOrganizationalUnitName().toLowerCase().startsWith(MainCompanyUnit.toLowerCase()))
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
