package com.bsep_sbz.PKI.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="serial_number", unique = false, nullable = true)
    private Long serialNumber;

    @Column(name="common_name", unique=false, nullable=false)
    private String commonName;

    @Column(name="organizationa_unit_name", unique=false, nullable=false)
    private String organizationalUnitName;

    @Column(name="revoked", unique=false, nullable=false)
    private boolean revoked;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Certificate> trustStoreCertificates;

    public Certificate() {

    }

    public Certificate(String commonName, String organizationalUnitName, boolean revoked) {
        this.commonName = commonName;
        this.organizationalUnitName = organizationalUnitName;
        this.revoked = revoked;
        this.trustStoreCertificates = new ArrayList<Certificate>();
    }

    public Certificate(String commonName, String organizationalUnitName) {
        this.commonName = commonName;
        this.organizationalUnitName = organizationalUnitName;
        this.revoked = false;
        this.trustStoreCertificates = new ArrayList<Certificate>();
    }

    public Certificate(Long serialNumber, String commonName, String organizationalUnitName) {
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.organizationalUnitName = organizationalUnitName;
        this.revoked = false;
        this.trustStoreCertificates = new ArrayList<Certificate>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<Certificate> getTrustStoreCertificates() { return trustStoreCertificates; }

    public void setTrustStoreCertificates(List<Certificate> trustStoreCertificates) { this.trustStoreCertificates = trustStoreCertificates; }

    public String getOrganizationalUnitName() { return organizationalUnitName; }

    public void setOrganizationalUnitName(String organizationalUnitName) { this.organizationalUnitName = organizationalUnitName; }
}
