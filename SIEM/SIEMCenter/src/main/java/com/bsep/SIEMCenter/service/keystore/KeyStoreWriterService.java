package com.bsep.SIEMCenter.service.keystore;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public interface KeyStoreWriterService {

    void loadKeyStore(String fileName, char[] password);

    void saveKeyStore(String fileName, char[] password);

    void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate);

    void writeCertificateInTrustStore(String alias, char[] trustStorePassword, Certificate certificate);
}
