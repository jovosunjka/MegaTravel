package com.bsep_sbz.PKI.service.keystore;

import com.bsep_sbz.PKI.model.IssuerData;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public interface KeyStoreReaderService {

    IssuerData readIssuerFromStore(Object fileOrFileName, String alias, char[] password, char[] keyPass);

    Certificate readCertificate(Object fileOrFileName, String keyStorePass, String alias);

    PrivateKey readPrivateKey(Object fileOrFileName, char[] keyStorePass, String alias, char[] pass);
}
