package com.bsep.SIEMCenter.service.keystore;


import java.security.PrivateKey;
import java.security.cert.Certificate;

public interface KeyStoreReaderService {

    Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias);

    PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass);
}
