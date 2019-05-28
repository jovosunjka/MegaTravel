package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.model.IssuerData;
import com.bsep_sbz.PKI.model.SubjectData;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface CertificateService {

    X509Certificate createCertificate(CertificateSigningRequest csr) throws Exception;

    boolean isRevokedById(Long certificateId) throws Exception;

    boolean isRevoked(Long serialNumber)throws Exception;

    SubjectData generateSubjectData(CertificateSigningRequest csr, PublicKey publicKey);

    IssuerData generateIssuerData(PrivateKey issuerKey, String commonName, String organizationName,
                                  String organizationalUnitName, String countryCode, String userId);

    void revoke(long serialNumber);
}
