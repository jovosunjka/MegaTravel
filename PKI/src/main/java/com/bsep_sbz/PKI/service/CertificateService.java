package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.dto.TrustStoreConfigDTO;
import com.bsep_sbz.PKI.model.Certificate;
import com.bsep_sbz.PKI.model.ChangedTrustStoreConfig;
import com.bsep_sbz.PKI.model.IssuerData;
import com.bsep_sbz.PKI.model.SubjectData;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateService {

    X509Certificate createCertificate(CertificateSigningRequest csr) throws Exception;

    boolean isRevokedById(Long certificateId) throws Exception;

    boolean isRevoked(Long serialNumber)throws Exception;

    SubjectData generateSubjectData(CertificateSigningRequest csr, PublicKey publicKey);

    IssuerData generateIssuerData(PrivateKey issuerKey, String commonName, String organizationName,
                                  String organizationalUnitName, String countryCode, String userId);

    void revoke(long serialNumber);

    List<Certificate> getNonRevokedCertiificates();

    void saveCertificate(X509Certificate certificate) throws CertificateEncodingException;

    void saveCertificate(Certificate certificate);

    File prepareTrustStoreFile(String organizationalUnitName, List<String> trustStoreCertificateOrganizationalUnitNames) throws Exception;

    void sendFile(File trustStoreFile, String organizationalUnitName) throws Exception;

    ChangedTrustStoreConfig isChangedTrustStoreConfig(TrustStoreConfigDTO trustStoreConfig, List<Certificate> nonRevokedCertificates);
}
