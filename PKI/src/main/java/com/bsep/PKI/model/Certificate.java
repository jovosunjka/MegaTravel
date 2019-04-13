package com.bsep.PKI.model;

import javax.persistence.*;

@Entity
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="common_name", unique=false, nullable=false)
    private String commonName;

    @Column(name="revoked", unique=false, nullable=false)
    private boolean revoked;

    public Certificate() {

    }

    public Certificate(String commonName, boolean revoked) {
        this.commonName = commonName;
        this.revoked = revoked;
    }

    public Certificate(String commonName) {
        this.commonName = commonName;
        this.revoked = false;
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
}
