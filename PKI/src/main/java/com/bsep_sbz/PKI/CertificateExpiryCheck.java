package com.bsep_sbz.PKI;

import com.bsep_sbz.PKI.model.ApplicationAddress;
import com.bsep_sbz.PKI.service.ApplicationAddressService;
import com.bsep_sbz.PKI.service.CertificateService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@Component
public class CertificateExpiryCheck {

    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private ApplicationAddressService applicationAddressService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${main_root_ca}")
    private String MainRootCa;

    @Value("${main_company_unit}")
    private String MainCompanyUnit;

    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private char[] keyStorePassword;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;

    //private int NUMBER_OF_DAYS_REMAINING = 5;
    private int NUMBER_OF_DAYS_REMAINING = 15;


    //https://www.baeldung.com/cron-expressions

    // Ova metoda ce se izvrsiti svaki dan u 23:59
    @Scheduled(cron = "${cetificate.expiry.check}")
    public void cronJob() {
        try {
            List<Certificate> rootCertificates = keyStoreReaderService.readCertificates(keyStore.getFile(), keyStorePassword, null, false);
            List<Certificate> certificates = keyStoreReaderService.readCertificates(trustStore.getFile(), trustStorePassword, null, true);


            Certificate rootCertificateThatLastExpires = rootCertificates.parallelStream().max((c1, c2) -> {
                Date d1 = ((X509Certificate) c1).getNotAfter();
                Date d2 = ((X509Certificate) c2).getNotAfter();
                return d1.compareTo(d2) ;
            }).get();
            /*Certificate mainRootCertificate = rootCertificates.stream()
                    .filter(root -> MainRootCa.equalsIgnoreCase(certificateService.getOrganizationalUnitName(root)))
                    .findFirst().get();*/

            if(certificateService.certificateExpiresFor((X509Certificate) rootCertificateThatLastExpires) < NUMBER_OF_DAYS_REMAINING) {
                try {
                    X509Certificate newRootCertificate = certificateService.createRootCertificate();

                    //List<com.bsep_sbz.PKI.model.Certificate> rootTrustStoreCertificates = certificateService.getTrustStoreCertificates(certificateService.getOrganizationalUnitName(rootCertificateThatLastExpires));
                    List<com.bsep_sbz.PKI.model.Certificate> nonRevokedCertificates = certificateService.getNonRevokedCertiificates();
                    nonRevokedCertificates.stream()
                            // izbacujemo root sertifikate
                            .filter(cer -> !cer.getOrganizationalUnitName().toLowerCase().startsWith(MainCompanyUnit.toLowerCase()))
                            .forEach(cert -> {
                                List<Certificate> newTrustStoreCertificates = new ArrayList<Certificate>();
                                List<Certificate> filteredTrustStoreCertificates = certificateService.filterCertificates(certificates, cert.getTrustStoreCertificates());
                                newTrustStoreCertificates.addAll(filteredTrustStoreCertificates);
                                newTrustStoreCertificates.addAll(rootCertificates);
                                newTrustStoreCertificates.add(newRootCertificate);

                                File trustStoreFile = null;
                                try {
                                    trustStoreFile = certificateService.prepareTrustStoreFile(cert.getOrganizationalUnitName(), null, newTrustStoreCertificates);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    certificateService.sendFile(trustStoreFile, cert.getOrganizationalUnitName());
                                    System.out.println("Poslao "+cert.getOrganizationalUnitName()+"-u njegov truststore.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            certificates.parallelStream()
                    .filter(cert -> certificateService.certificateExpiresFor((X509Certificate) cert) < NUMBER_OF_DAYS_REMAINING)
                    .map(cert -> certificateService.getOrganizationalUnitName(cert))
                    .forEach(organizationalUnitName -> {
                                for(int i = 0; i < 3; i++) {
                                    ApplicationAddress applicationAddress = applicationAddressService.getApplicationAddress(organizationalUnitName);
                                    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(applicationAddress.getUrl(), null, Void.class);
                                    if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                                        break;
                                    } else {
                                        // odspavaj 2min, pa pokusaj ponovo
                                        try {
                                            Thread.sleep(2 * 60 * 1000); // 2 min
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
