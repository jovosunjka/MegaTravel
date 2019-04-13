package com.bsep.PKI.controller;

import com.bsep.PKI.dto.CertificateSigningRequest;
import com.bsep.PKI.model.*;
import com.bsep.PKI.repository.UserRepository;
import com.bsep.PKI.service.CertificateService;
import com.bsep.PKI.service.certificate.CertificateGeneratorService;
import com.bsep.PKI.service.keystore.KeyStoreReaderService;
import com.bsep.PKI.service.keystore.KeyStoreWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCertificate(@RequestBody CertificateSigningRequest csr) {
        try {
            certificateService.createCertificate(csr);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("Sertifikat je uspesno kreiran!", HttpStatus.CREATED);
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
}
