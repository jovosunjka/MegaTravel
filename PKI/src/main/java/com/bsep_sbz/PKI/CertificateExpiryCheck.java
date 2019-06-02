package com.bsep_sbz.PKI;

import com.bsep_sbz.PKI.model.ApplicationAddress;
import com.bsep_sbz.PKI.service.ApplicationAddressService;
import com.bsep_sbz.PKI.service.CertificateService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreReaderService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;

    private int NUMBER_OF_DAYS_REMAINING = 5;


    //https://www.baeldung.com/cron-expressions

    // Ova metoda ce se izvrsiti svaki dan u 23:59
    @Scheduled(cron = "${cetificate.expiry.check}")
    public void cronJob() {
        List<Certificate> certificates = null;

        try {
            certificates = keyStoreReaderService.readCertificates(trustStore.getFile(), trustStorePassword, null, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        certificates.parallelStream()
                .filter(cert -> certificateService.certificateExpiresFor((X509Certificate) cert) < NUMBER_OF_DAYS_REMAINING)
                .map(cert -> getOrganizationalUnitName(cert))
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
    }

    private String getOrganizationalUnitName(Certificate certificate) {
        try {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            X500Name x500Name = new JcaX509CertificateHolder(x509Certificate).getSubject();
            String organizationalUnitName = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue());
            return organizationalUnitName;
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
