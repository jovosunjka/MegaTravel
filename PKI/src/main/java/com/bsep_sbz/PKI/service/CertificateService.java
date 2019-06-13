package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.dto.TrustStoreConfigDTO;
import com.bsep_sbz.PKI.model.*;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateService {

    X509Certificate createCertificate(CertificateSigningRequest csr, CertificateType certificateType) throws Exception;

    X509Certificate createRootCertificate() throws Exception;

    boolean isRevokedById(Long certificateId) throws Exception;

    boolean isRevoked(Long serialNumber)throws Exception;

    SubjectData generateSubjectData(CertificateSigningRequest csr, CertificateType certificateType, PublicKey publicKey);

    IssuerData generateIssuerData(PrivateKey issuerKey, String commonName, String organizationName,
                                  String organizationalUnitName, String countryCode, String userId);

    CertificateSigningRequest prepareRootCaIssuerCSR();

    void revoke(long serialNumber);

    List<Certificate> getNonRevokedCertiificates();

    void saveCertificate(X509Certificate certificate) throws CertificateEncodingException;

    void saveCertificate(Certificate certificate);

    File prepareTrustStoreFile(String organizationalUnitName, List<String> trustStoreCertificateOrganizationalUnitNames, List<java.security.cert.Certificate> trustStoreCertificates) throws Exception;

    void sendFile(File trustStoreFile, String organizationalUnitName) throws Exception;

    long certificateExpiresFor(X509Certificate certificate);

    ChangedTrustStoreConfig isChangedTrustStoreConfig(TrustStoreConfigDTO trustStoreConfig, List<Certificate> nonRevokedCertificates);


    List<Certificate> getTrustStoreCertificates(String organizationalUnitName);

    String getOrganizationalUnitName(java.security.cert.Certificate certificate);

    List<java.security.cert.Certificate> filterCertificates(List<java.security.cert.Certificate> certificates, List<com.bsep_sbz.PKI.model.Certificate> certificatesDB);

    void replaceTrustStoresOfOtherApplications(Certificate newCertificate);
}

