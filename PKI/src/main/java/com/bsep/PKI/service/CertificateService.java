package com.bsep.PKI.service;

import com.bsep.PKI.dto.CertificateSigningRequest;
import com.bsep.PKI.model.IssuerData;
import com.bsep.PKI.model.SubjectData;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface CertificateService {

    X509Certificate createCertificate(CertificateSigningRequest csr) throws Exception;

    boolean isRevoked(Long certificateId) throws Exception;

    SubjectData generateSubjectData(CertificateSigningRequest csr, PublicKey publicKey);

    IssuerData generateIssuerData(PrivateKey issuerKey, String commonName, String organizationName,
                                  String organizationalUnitName, String countryCode, String userId);
}
