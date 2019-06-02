package com.bsep_sbz.PKI.service.keystore;

import com.bsep_sbz.PKI.model.IssuerData;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

public interface KeyStoreReaderService {

    IssuerData readIssuerFromStore(Object fileOrFileName, String alias, char[] password, char[] keyPass);

    Certificate readCertificate(Object fileOrFileName, char[] keyStorePass, String alias);

    List<Certificate> readCertificates(Object fileOrFileName, char[] keyStorePass, List<String> aliases, boolean trustStore);

    PrivateKey readPrivateKey(Object fileOrFileName, char[] keyStorePass, String alias, char[] pass);

    IssuerData getIssuerData(Object fileOrFileName, String alias, char[] password, char[] keyPass, X509Certificate certificate);
}
