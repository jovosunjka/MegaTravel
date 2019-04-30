package com.bsep_sbz.SIEMCenter.service.interfaces;


import com.bsep_sbz.SIEMCenter.controller.dto.CertificateSigningRequest;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface ICertificateService {

    KeyPair generateKeyPair();

    CertificateSigningRequest prepareCSR(PublicKey publicKey);

    void saveCertificate(PrivateKey privateKey, X509Certificate certificate) throws IOException;

    void saveCertificateInTrustStore(X509Certificate certificate) throws IOException;

    void loadCertificate(String certificatePath);

    X509Certificate getCertificate(String certificateStr);
}
