package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.controller.dto.CertificateSigningRequest;
import com.bsep.SIEMCenter.service.interfaces.ICertificateService;
import com.bsep.SIEMCenter.service.keystore.KeyStoreReaderService;
import com.bsep.SIEMCenter.service.keystore.KeyStoreWriterService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@Service
public class CertificateService implements ICertificateService
{
    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private KeyStoreWriterService keyStoreWriterService;

    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${server.ssl.key-alias}")
    private String aliasKeyStore;

    @Value("${trust-store-password}")
    private String trustStorePassword;

    @Value("${my-url}")
    private String myUrl;

    @Override
    public CertificateSigningRequest prepareCSR(PublicKey publicKey) {
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        CertificateSigningRequest csr = new CertificateSigningRequest("SiemCenter", "MegaTravel",
                "MegaTravel SiemCenter", "RS", "61897", publicKeyStr, myUrl);
        return csr;
    }

    @Override
    public void saveCertificate(String commonName, PrivateKey privateKey, X509Certificate certificate) {
        writeCertificateInFile(commonName, certificate);
        keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        keyStoreWriterService.write(aliasKeyStore, privateKey, keyStorePassword.toCharArray(), certificate);
        keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
    }

    @Override
    public void saveCertificateInTrustStore(String commonName, X509Certificate certificate) {
        writeCertificateInFile(commonName, certificate);
        keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        keyStoreWriterService.writeCertificateInTrustStore(certificate.getSubjectX500Principal().getName(),
                trustStorePassword.toCharArray(), certificate);
        keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeCertificateInFile(String commonName, X509Certificate certificate) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(keyStorePath + "/" + commonName +".cer");
            out.write(certificate.getEncoded());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
