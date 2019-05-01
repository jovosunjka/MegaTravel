package com.bsep_sbz.PKI.dto;

import com.bsep_sbz.PKI.model.CertificateType;

public class CertificateSigningRequest {
    private String commonName;
    //private String surname;
    //private String givenName;
    private String organizationName;
    private String organizationalUnitName;
    private String countryCode;
    //private String emailAddress;
    private String userId;
    private CertificateType certificateType;
    private String publicKey;  // ovaj atribut je null za certifikate za CA-a
    private String destinationUrl; // nije obavezan

    public CertificateSigningRequest() {

    }

    public CertificateSigningRequest(String commonName, /*String surname, String givenName,*/ String organizationName,
                                     String organizationalUnitName, String countryCode, /*String emailAddress,*/ String userId,
                                     String publicKey, CertificateType certificateType, String destinationUrl) {
        this.commonName = commonName;
        //this.surname = surname;
        //this.givenName = givenName;
        this.organizationName = organizationName;
        this.organizationalUnitName = organizationalUnitName;
        this.countryCode = countryCode;
        //this.emailAddress = emailAddress;
        this.userId = userId;
        this.publicKey = publicKey;
        this.certificateType = certificateType;
        this.destinationUrl = destinationUrl;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /*public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }*/

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /*
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }*/

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }
}